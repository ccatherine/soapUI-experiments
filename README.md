# soapUI-experiments
Experiments with SOAP UI

# generateDynamicResponse

In SOAPUI, in the mock response of the intended response result put:
${responseRequestedPortsMessage} 
Then in the script copy whatever you need from the generateDynamicResponse file. The implementation is quite attached with my needs for this mock however some of the functions can be used without changes. 
The structure of the data (AUTORESPONSE table), looked like this:

| STATEID       | ATTNAME       | ATTVALUE  | PATH  |
| ------------- |:-------------:| -----:| -----:|
| 1      | name | Catherine Francisco | 0:mainData |
| 1      | occupation      |   Software Developer | 0:mainData |
| 1 | country      |    Portugal | 1: address|
| 1 | streetName     |  Rua | 1: address|
| 1 | postCode      |    34532 | 1: address|
