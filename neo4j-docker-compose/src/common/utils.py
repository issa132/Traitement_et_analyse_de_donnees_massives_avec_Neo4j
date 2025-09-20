import re



def extract_bedroom(row):
    pattern = re.compile(r'(\d+)\s*bedroom', re.IGNORECASE)
    match = pattern.search(str(row))
    return int(match.group(1)) if match else None


def extract_beds(row):
    pattern = re.compile(r'(\d+)\s*beds', re.IGNORECASE)
    match = pattern.search(str(row))
    return int(match.group(1)) if match else None


def extract_bath(row):
    pattern = re.compile(r'(\d+)\s*bath', re.IGNORECASE)
    match = pattern.search(str(row))
    return int(match.group(1)) if match else None


def transform_amenities(row):
    return '|'.join(eval(row))


def transform_host_verifications(row):
    return '|'.join(eval(row))


def filter_by_location(df):
    pattern = re.compile(r"montreal", re.IGNORECASE)
    return df[df['host_location'].apply(lambda x: bool(pattern.search(str(x))))]
