import pandas as pd
import psycopg2
import plotly.graph_objs as go
import sys
import json
from sqlalchemy import create_engine

# PostgreSQL 연결 정보: test db
DB_CONFIG = {
    'dbname': 'favicon_db',
    'user': 'favicon',
    'password': 'favicon2024',
    'host': 'favicon-database.c5q6se0mu4pm.ap-northeast-2.rds.amazonaws.com',
    'port': '5432'
}

# SQLAlchemy 엔진 (pandas용)
engine = create_engine(
    f"postgresql+psycopg2://{DB_CONFIG['user']}:{DB_CONFIG['password']}@{DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['dbname']}"
)

class DataInsert:
    def __init__(self):
        self.conn = psycopg2.connect(**DB_CONFIG)
        self.cursor = self.conn.cursor()

    def get_theme_id(self, theme):
        self.cursor.execute("SELECT dataset_theme_id FROM dataset_theme WHERE theme = %s", (theme,))
        return self.cursor.fetchone()[0]

    def ensure_region(self, region_name):
        self.cursor.execute("INSERT INTO region (region_name) VALUES (%s) ON CONFLICT DO NOTHING", (region_name,))

    def get_or_create_indicator(self, theme_id, indicator_name, unit):
        self.cursor.execute(
            "SELECT indicator_id FROM indicator WHERE indicator_name = %s AND dataset_theme_id = %s",
            (indicator_name, theme_id)
        )
        result = self.cursor.fetchone()
        if result:
            return result[0]
        else:
            self.cursor.execute(
                "INSERT INTO indicator (dataset_theme_id, indicator_name, unit) VALUES (%s, %s, %s) RETURNING indicator_id",
                (theme_id, indicator_name, unit)
            )
            return self.cursor.fetchone()[0]

    def insert_data(self, climate_path, disease_path):
        climate_df = pd.read_csv(climate_path, encoding='utf-8')
        disease_df = pd.read_csv(disease_path, encoding='utf-8')

        climate_df.rename(columns={
            '연도': 'year_month', '지역': 'region',
            '평균기온': 'avg_temp', '평균최저기온': 'min_temp', '평균최고기온': 'max_temp', '단위': 'unit'
        }, inplace=True)

        disease_df.rename(columns={
            '항목': 'indicator_name', '지역': 'region', '연도': 'year_month', '환자수': 'value', '단위': 'unit'
        }, inplace=True)

        climate_theme_id = self.get_theme_id('기후')
        disease_theme_id = self.get_theme_id('질병')

        climate_df['year_month'] = climate_df['year_month'].str.strip()
        disease_df['year_month'] = disease_df['year_month'].str.strip()

        for _, row in climate_df.iterrows():
            self.ensure_region(row['region'])
            date_val = pd.to_datetime(row['year_month']).date()

            for name, col in [('평균기온', 'avg_temp'), ('평균최저기온', 'min_temp'), ('평균최고기온', 'max_temp')]:
                indicator_id = self.get_or_create_indicator(climate_theme_id, name, row['unit'])
                self.cursor.execute(
                    "INSERT INTO value (indicator_id, dataset_theme_id, region_name, date, value) VALUES (%s, %s, %s, %s, %s)",
                    (indicator_id, climate_theme_id, row['region'], date_val, row[col])
                )

        for _, row in disease_df.iterrows():
            self.ensure_region(row['region'])
            date_val = pd.to_datetime(row['year_month']).date()
            indicator_id = self.get_or_create_indicator(disease_theme_id, row['indicator_name'], row['unit'])
            self.cursor.execute(
                "INSERT INTO value (indicator_id, dataset_theme_id, region_name, date, value) VALUES (%s, %s, %s, %s, %s)",
                (indicator_id, disease_theme_id, row['region'], date_val, row['value'])
            )

        self.conn.commit()
        self.cursor.close()
        self.conn.close()
        print("모든 데이터 삽입 완료!")


class Visualize:
    def __init__(self):
        self.conn = psycopg2.connect(**DB_CONFIG)

    def get_dataframes(self, region, disease_name):
        climate_query = f"""
            SELECT date AS 연도, region_name AS 지역,
                   MAX(CASE WHEN i.indicator_name = '평균기온' THEN v.value END) AS 평균기온,
                   MAX(CASE WHEN i.indicator_name = '평균최저기온' THEN v.value END) AS 평균최저기온,
                   MAX(CASE WHEN i.indicator_name = '평균최고기온' THEN v.value END) AS 평균최고기온
            FROM value v
            JOIN indicator i ON v.indicator_id = i.indicator_id
            JOIN dataset_theme dt ON v.dataset_theme_id = dt.dataset_theme_id
            WHERE dt.theme = '기후' AND region_name = '{region}'
            GROUP BY 연도, 지역
            ORDER BY 연도
        """

        disease_query = f"""
            SELECT date AS 연도, region_name AS 지역, i.indicator_name AS 항목, v.value AS 환자수
            FROM value v
            JOIN indicator i ON v.indicator_id = i.indicator_id
            JOIN dataset_theme dt ON v.dataset_theme_id = dt.dataset_theme_id
            WHERE dt.theme = '질병' AND i.indicator_name = '{disease_name}' AND region_name = '{region}'
            ORDER BY 연도
        """

        climate_df = pd.read_sql_query(climate_query, engine)
        disease_df = pd.read_sql_query(disease_query, engine)
        return climate_df, disease_df

    def generate_plot_json(self, region, disease_name):
        climate_df, disease_df = self.get_dataframes(region, disease_name)

        # 날짜 변환 및 merge
        climate_df['연도'] = pd.to_datetime(climate_df['연도'])
        disease_df['연도'] = pd.to_datetime(disease_df['연도'])
        merged_df = pd.merge(climate_df, disease_df, on=['연도', '지역'], how='inner')

        merged_df = merged_df.groupby(['연도'], as_index=False).agg({
            '평균기온': 'mean',
            '환자수': 'mean'
        })

        # Plotly 시각화
        fig = go.Figure()

        fig.update_layout(
            title=f"{region} 지역 평균 기온과 {disease_name} 환자 수 비교",
            title_font_size=22,
            xaxis=dict(
                title='연도 (월별)',
                showgrid=True,
                gridcolor='lightgrey',
                tickfont=dict(size=12)
            ),
            yaxis=dict(
                title='평균 기온(℃)',
                showgrid=True,
                gridcolor='lightgrey',
                tickfont=dict(size=12)
            ),
            yaxis2=dict(
                title=f'{disease_name} 환자 수',
                overlaying='y',
                side='right',
                showgrid=False,
                tickfont=dict(size=12)
            ),
            width=1600,
            height=800,
            plot_bgcolor='white',
            bargap=0.2,
            legend=dict(
                x=0.01, y=1.15,
                orientation="h",
                font=dict(size=14)
            ),
            margin=dict(l=60, r=60, t=100, b=60)
        )


        # 평균기온 (막대그래프)
        fig.add_trace(
            go.Bar(
                x=merged_df['연도'],
                y=merged_df['평균기온'],
                name='평균 기온',
                marker_color='#658147',
                yaxis='y1'
            )
        )

        # 감기 환자수 (꺾은선 그래프)
        fig.add_trace(
            go.Scatter(
                x=merged_df['연도'],
                y=merged_df['환자수'],
                name=f'{disease_name} 환자 수',
                mode='lines+markers',
                marker=dict(color='#F27669', size=6),
                line=dict(color='#F27669', width=2),
                yaxis='y2'
            )
        )

        fig.update_layout(
            barmode='group',
            title=f"{region} 지역 평균 기온과 {disease_name} 환자 수 비교",
            xaxis=dict(title='연도'),
            yaxis=dict(title='평균 기온(℃)', side='left'),
            yaxis2=dict(
                title=f'{disease_name} 환자 수',
                overlaying='y',
                side='right'
            ),
            legend=dict(x=0, y=1.15, orientation="h")
        )
        #fig.show(renderer="browser")
        return fig.to_json()


if __name__ == '__main__':

    # 데이터 삽입
    try:
        inserter = DataInsert()
        inserter.insert_data("resources/기상청_월별_processed.csv", "resources/기후_감기_건강보험심사평가원.csv")
    except Exception as e:
        print("insert data 실패:", e)

    if len(sys.argv) != 6:
        error_json = json.dumps({"error": "Invalid number of arguments"})
        print(error_json)
        sys.exit(1)

    theme1 = sys.argv[1]
    theme2 = sys.argv[2]
    region = sys.argv[3]
    start = sys.argv[4]
    end = sys.argv[5]

    try:
        visualizer = Visualize()
        result_json = visualizer.generate_plot_json(region=region, disease_name=theme1)
        print(result_json)

    except Exception as e:
        error_json = json.dumps({"error": str(e)})
        print(error_json)
        sys.exit(1)
