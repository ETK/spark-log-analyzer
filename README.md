# Spark Log Analyzer

## 1 run spark
```
bin/spark-submit --class com.sectong.sparkapacheloganalysis.LogAnalyzer ~/spark-log-analysis-0.0.1-SNAPSHOT.jar  ~/access.log
```

OUTPUT:
```
Content Size Avg: 1231, Min: 0, Max: 93445                                      
Response code counts: [(301,4), (304,408), (200,56), (404,260)]                 
IPAddresses > 10 times: [112.65.201.61]                                         
Top Endpoints: [(/hello,148), (/,48), (/com,48), (/111111111111111111111111,48), (/logyun-webui/css/font-awesome.css,28), (/logyun-webui/css/bootstrap.min.css,28), (/logyun-webui/js/bootstrap.js,28), (/logyun-webui/css/pages/dashboard.css,28), (/logyun-webui/data/data.tsv,28), (/logyun-webui/js/d3.v3.min.js,28)]
```

## 2 run spark SQL
```
bin/spark-submit --class com.sectong.sparkapacheloganalysis.LogAnalyzerSQL ~/spark-log-analysis-0.0.1-SNAPSHOT.jar  ~/access.log
```
OUTPUT:
```
Content Size Avg: 1231, Min: 0, Max: 93445                                      
Response code counts: [(404,260), (200,56), (304,408), (301,4)]
IPAddresses > 10 times: [112.65.201.61]
Top Endpoints: [(/hello,148), (/111111111111111111111111,48), (/,48), (/com,48), (/logyun-webui/data/data.tsv,28), (/logyun-webui/css/style.css,28), (/logyun-webui/css/pages/dashboard.css,28), (/logyun-webui/css/bootstrap-responsive.min.css,28), (/logyun-webui/css/font-awesome.css,28), (/logyun-webui/data/bullets.json,28)]
```

## 3 run spark streaming
### apachelog config
in your apache config file:
```
TransferLog "|nc -kl -p 9999"
```

### run
```
bin/spark-submit --class com.sectong.sparkapacheloganalysis.LogAnalyzerStreaming ~/spark-log-analysis-0.0.1-SNAPSHOT.jar localhost 9999
```
streaming OUTPUT:
```
Content Size Avg: 179, Min: 179, Max: 179
Response code counts: [(404,337)]
IPAddresses > 10 times: [112.65.201.61]
Top Endpoints: [(/11111111111111111111111,337)]
Content Size Avg: 179, Min: 179, Max: 179
Response code counts: [(404,184)]
IPAddresses > 10 times: [112.65.201.61]
Top Endpoints: [(/11111111111111111111111,184)]
No access logs in this time interval
No access logs in this time interval
No access logs in this time interval
No access logs in this time interval
```

