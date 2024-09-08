# Ad Metrics Processor

This Scala project processes JSON data related to ad impressions and clicks, calculates relevant metrics, and makes recommendations for the top advertisers.

## Overview

The project is designed to read and parse JSON data from input files, transform the data into usable Scala objects, process the data to calculate various metrics, and provide recommendations for the top 5 advertisers to display for each app and country combination.

## Implementation Details

### 1. Data Models

- **Impression and Click Classes**:  
  Two case classes, `Impression` and `Click`, are defined with fields matching the expected structure of the corresponding JSON data.
- **JSON Formatting**:  
  `jsonFormat4` and `jsonFormat2` methods from `spray.json.DefaultJsonProtocol` are used to create `JsonFormat` instances for `Impression` and `Click`, respectively.

### 2. Reading and Parsing Input Files

- Input file paths are defined, and files are read using `scala.io.Source`.
- The file contents are parsed as JSON using `scala.util.parsing.json` and converted into lists of `Map[String, JsValue]`.

### 3. Transforming JSON Data

- The `flatMap` method is used to transform each `Map[String, JsValue]` in the list into a `List[Impression]` or `List[Click]`.
- Values are extracted from the `JsValue` objects in the map to construct new `Impression` or `Click` objects.
- If any errors occur during parsing, an error message is printed, and an empty list is returned.

### 4. Processing Data

- Impressions are grouped by `(app_id, country_code)` pairs using the `groupBy` method.
- The resulting `Map` is processed using `mapValues` to generate a tuple `(impressionCount, clickCount, revenueSum)` for each group.
- The clicks list is used to compute `clickCount` and `revenueSum` based on whether the `impression_id` of each `Click` object matches an `Impression` object in the group.

### 5. Generating Output

- A new list of `JsObject` is created based on the `(app_id, country_code)` pairs and the computed values.

### 6. Calculating Metrics and Recommendations

- Metrics are calculated for various dimensions.
- The `topAdvertisersByAppAndCountry` method provides recommendations for the top 5 `advertiser_ids` to display for each app and country combination.

## Result

- The project outputs metrics for different dimensions and suggests the top 5 advertiser IDs for each app-country pair.

## Getting Started

1. **Clone the Repository**:  
   ```bash
   git clone https://github.com/yourusername/ad-metrics-processor.git
