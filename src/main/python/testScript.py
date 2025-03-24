import sys
import json


def analysis(dataset_theme, dataset_region, period_start, period_end):
    result = {
        "theme": dataset_theme,
        "region": dataset_region,
        "start": period_start,
        "end": period_end
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
