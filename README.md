# soapUI-experiments
Experiments with SOAP UI creating dynamic data structures as mock responses

# generateDynamicResponse

In SOAPUI, in the mock response of the intended response result put:
${responseGetDataMessage} 
Then in the script copy whatever you need from the generateDynamicResponse file. The implementation is quite attached with my needs for this mock however some of the functions can be used without changes. 
The structure of the data (AUTORESPONSE table), looked like this:

| STATEID       | ATTNAME       | ATTVALUE  | PATH  |
| ------------- |:-------------:| -----:| -----:|
| 1      | name | Maria | 0:mainData |
| 1      | occupation      |   Software Developer | 0:mainData |
| 1 | country      |    Portugal | 1: address|
| 1 | streetName     |  Rua | 1: address|
| 1 | postCode      |    34532 | 1: address|

This is what my base payload structure looked like (request and response):

```xml
<soapenv:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
   <soapenv:Header/>
   <soapenv:Body>
      <ser:getDataRequest>
		<status>Processed with error</status>
		<status>Not Processed</status>
      </ser:getDataRequest>
   </soapenv:Body>
</soapenv:Envelope>

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <soapenv:Body>
      <ns1:getDataResponse>
         <getDataReturn>
         </getDataReturn>
      </ns1:getDataResponse>
   </soapenv:Body>
</soapenv:Envelope>
```

At the end, the payload would look like this:
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
   <soapenv:Body>
      <ns1:getDataResponse>
         <getDataReturn>
         <mainData>
          <name>Maria</name>
          <occupation>Software Developer</occupation>
          <address>
            <country>Portugal</country>
            <streetName>Rua</streetName>
            <postCode>34532</postCode>
          </address>
         </mainData>
         </getDataReturn>
      </ns1:getDataResponse>
   </soapenv:Body>
</soapenv:Envelope>
```
