The Impression and Click case classes are defined, each with fields matching the expected fields in the corresponding JSON data. The jsonFormat4 and jsonFormat2 methods from spray.json.DefaultJsonProtocol are used to create JsonFormat instances for Impression and Click, respectively.
In the main method, the input file paths are defined and the files are read using scala.io.Source. The contents of the files are then parsed as JSON using scala.util.parsing.json and converted to lists of Map[String, JsValue]. The flatMap method is used to transform each Map[String, JsValue] in the list to a List[Impression] or List[Click]. This involves extracting values from the JsValues in the map and constructing new Impression or Click objects using these values. If any errors occur during parsing, the catch block prints an error message and returns an empty list of Impression or Click.
Next, the impressions and clicks lists are processed to generate the desired output. The groupBy method is used to group the impressions by (app_id, country_code) pairs. The resulting Map is then processed using mapValues to generate a tuple of (impressionCount, clickCount, revenueSum) for each group. The clicks list is used to compute clickCount and revenueSum based on whether the impression_id of each Click object matches an Impression object in the group. Finally, a new List of JsObject is created based on the (app_id, country_code) pairs and the computed values.
and
result:
Calculate metrics for some dimensions
and
topAdvertisersByAppAndCountry:
Make a recommendation for the top 5 advertiser_ids to display for each app and country combination.
