This Scala project processes JSON data related to ad impressions and clicks, calculates relevant metrics, and makes recommendations for the top advertisers.

Implementation Details
Data Models:

Two case classes, Impression and Click, are defined with fields that match the expected structure of the corresponding JSON data.
The jsonFormat4 and jsonFormat2 methods from spray.json.DefaultJsonProtocol are used to create JsonFormat instances for Impression and Click, respectively.
Reading and Parsing Input Files:

Input file paths are defined, and files are read using scala.io.Source.
The file contents are parsed as JSON using scala.util.parsing.json and converted into lists of Map[String, JsValue].
Transforming JSON Data:

The flatMap method is used to transform each Map[String, JsValue] in the list into a List[Impression] or List[Click].
This involves extracting values from the JsValues in the map and constructing new Impression or Click objects.
If any errors occur during parsing, an error message is printed, and an empty list is returned for either Impression or Click.
Processing Data:

Impressions are grouped by (app_id, country_code) pairs using the groupBy method.
The resulting Map is processed using mapValues to generate a tuple (impressionCount, clickCount, revenueSum) for each group.
The clicks list is processed to compute clickCount and revenueSum based on whether the impression_id of each Click object matches an Impression object in the group.
Generating Output:

A new list of JsObject is created based on the (app_id, country_code) pairs and the computed values.
Calculating Metrics and Recommendations:

Metrics are calculated for different dimensions.
The topAdvertisersByAppAndCountry method makes a recommendation for the top 5 advertiser_ids to display for each app and country combination.
Result
The project calculates metrics for various dimensions and provides a list of the top 5 advertiser IDs to display for each app-country pair.
