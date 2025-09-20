import pytest
import pandas as pd

from src.common.utils import (
    extract_beds,
    extract_bath,
    extract_bedroom,
    filter_by_location,
    transform_amenities,
    transform_host_verifications,
)



def test_extract_bedroom():
    row1 = "2 bedrooms"
    row2 = "No bedroom"
    row3 = "3 beds"
    
    assert extract_bedroom(row1) == 2
    assert extract_bedroom(row2) is None
    assert extract_bedroom(row3) is None


def test_extract_beds():
    row1 = "2 beds"
    row2 = "No bed"
    row3 = "3 bedrooms"
    
    assert extract_beds(row1) == 2
    assert extract_beds(row2) is None
    assert extract_beds(row3) is None


def test_extract_bath():
    row1 = "1 bath"
    row2 = "No bath"
    row3 = "2 baths"
    
    assert extract_bath(row1) == 1
    assert extract_bath(row2) is None
    assert extract_bath(row3) is 2


def test_transform_amenities():
    row = "['Wifi', 'Kitchen', 'TV']"
    expected_result = "Wifi|Kitchen|TV"
    
    assert transform_amenities(row) == expected_result


def test_transform_host_verifications():
    row = "['email', 'phone', 'government_id']"
    expected_result = "email|phone|government_id"
    
    assert transform_host_verifications(row) == expected_result


def test_filter_by_location():
    df = pd.DataFrame({
        "host_location": ["Montreal, Canada", "Toronto, Canada", "Vancouver, Canada"]
    })
    
    expected_result = pd.DataFrame({
        "host_location": ["Montreal, Canada"]
    })
    
    filtered_df = filter_by_location(df)
    
    assert filtered_df.equals(expected_result)


# Exécuter les tests avec pytest
pytest.main()
