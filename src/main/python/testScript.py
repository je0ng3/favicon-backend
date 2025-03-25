import sys
import json


def analysis(dataset_theme, dataset_region, period_start, period_end):

    result = {
        "result": "분석된 시각화 json입니다",
        "theme": dataset_theme+"에 대해서",
        "region": dataset_region+"의 지역에서",
        "start": period_start+"부터",
        "end": period_end+"까지의 결과를 시각화했습니다."
    }
    print(json.dumps(result))


if __name__ == "__main__":

    if len(sys.argv) != 5:
        print(json.dumps({"error": "Invalid number of arguments"}))
        sys.exit(1)

    theme = sys.argv[1]
    region = sys.argv[2]
    start = sys.argv[3]
    end = sys.argv[4]

    analysis(theme, region, start, end)
