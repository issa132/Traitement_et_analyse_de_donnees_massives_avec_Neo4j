import argparse
import pandas as pd

from common.utils import (
    extract_beds,
    extract_bath,
    extract_bedroom,
    filter_by_location,
    transform_amenities,
    transform_host_verifications,
)


def main(url, output_file):
    df = pd.read_csv(url)

    deleted_columns = [
        "description",
        "host_about",
        "beds_extracted",       # transform column
        "bedrooms_extracted",   # transform column
        "bathrooms_extracted",  # transform column
        "neighborhood_overview",
    ]

    df["beds_extracted"] = df["name"].apply(extract_beds)
    df["bathrooms_extracted"] = df["name"].apply(extract_bath)
    df["bedrooms_extracted"] = df["name"].apply(extract_bedroom)

    df["beds"].fillna(df["beds_extracted"], inplace=True)
    df["beds_extracted"].fillna(df["beds"], inplace=True)

    df["bathrooms"].fillna(df["bathrooms_extracted"], inplace=True)
    df["bathrooms_extracted"].fillna(df["bathrooms"], inplace=True)

    df["bedrooms"].fillna(df["bedrooms_extracted"], inplace=True)
    df["bedrooms_extracted"].fillna(df["bedrooms"], inplace=True)

    df["amenities"] = df["amenities"].apply(transform_amenities)
    df["host_verifications"] = df["host_verifications"].apply(
        transform_host_verifications
    )

    df = filter_by_location(df)
    df = df.drop(columns=deleted_columns)

    df.to_csv(output_file, index=False)
    print(f"Données filtrées sauvegardées dans le fichier : {output_file}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    url = parser.add_argument("-u", "--url", help="URL du fichier CSV")
    output = parser.add_argument("-o", "--output", help="Chemin du fichier de sortie")

    args = parser.parse_args()
    main(args.url, args.output)
