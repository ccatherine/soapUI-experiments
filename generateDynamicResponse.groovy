/*
 * creates the sql statement based on the status that arrive in the request
 */
def generateSQLStatementBasedOnStatus(requestHolder)
{
	// loop item nodes in response message
	def statusArray = [];
	def toDuplicate;
	for( node in requestHolder['//status'] )
		statusArray.add(node.toString());
	def sqlStatement = "select stateid from possiblestates where state in (";
	for( def i = 0; i < statusArray.size; i++ )
	{
		if (i == statusArray.size -1)
			sqlStatement+= "'"+statusArray[i]+"')";
		else
			sqlStatement+= "'"+statusArray[i]+"',";
	}
	return sqlStatement;
}

/*
 * get the response base - same project as the mock within specific testsuite
 */
def getResponseBase(testStepBase)
{
	def cenas = context.mockService.project.getTestSuiteByName("TesteAuto").getTestCaseByName("baseMessage").getTestStepByName(testStepBase);
	def testStepContext = new com.eviware.soapui.impl.wsdl.testcase.WsdlTestRunContext(cenas);
	def groovyUtils2 = new com.eviware.soapui.support.GroovyUtils( testStepContext ) ;
	def teststepname = cenas.getName().toString();
	def inputHolder = teststepname + "#Response";
	return groovyUtils2.getXmlHolder(inputHolder);
}

/*
 * create containers based on configuration
 */
def calculateContainersToCreate(mapPathAtt)
{
	def maxDepth = -1;
	def toCreate = [];
	for(hashMapInstance in mapPathAtt.descendingMap()) 
	{
		def path = hashMapInstance.getKey();
		def pathList = path.tokenize( ':' );
		if (maxDepth < pathList[0])
		{
			maxDepth = pathList[0].toInteger();
			toCreate[maxDepth] = pathList[1];
		}
	}
	return toCreate;
}

/*
 * create full data structure, based on configuration of autoResponse table
 */
def createSkeleton(currentDepth, maxDepth, parentNode,arr,mapAtt)
{
	def doc = parentNode.getOwnerDocument();
	if (currentDepth == maxDepth)
		return parentNode;
	else //create container
	{
		def createPath = currentDepth+':'+arr[currentDepth];
		def attributesToAdd = mapAtt.get(createPath);
		def newNode = doc.createElement(arr[currentDepth]);
		createSimpleNodeBasedHash(newNode,doc,attributesToAdd);
		def depthPlus1 = currentDepth+1;
		childNode = createSkeleton(depthPlus1, maxDepth, newNode,arr,mapAtt);
		parentNode.insertBefore(newNode, parentNode.getFirstChild());
	}
}

/*
 * main function which will calculate how many containers are there and create the structure
 */
def createStruct(mapPathAtt,parentNode)
{
	def containersToCreate = calculateContainersToCreate(mapPathAtt);
	def maxDepth = containersToCreate.size()-1;
	createSkeleton(0, maxDepth, parentNode,containersToCreate,mapPathAtt);
}

/*
 * simple node structure, create element and add text. element will look like this: <test>Catherine</test>; test - element name ; Catherine - text node
 */
def createSimpleNode(node,doc,attArray)
{
	for( att in attArray )
	{
		def newElement = doc.createElement(att[0]);
		newElement.appendChild(doc.createTextNode(att[1]));
		node.appendChild(newElement);	
	}
	return node;
}

/*
 * simple node structure, same as createSimpleNode function but based on the hash originated from database data
 */
def createSimpleNodeBasedHash(node,doc,hashm)
{
	for(entry in hashm.entrySet()) {
		def key = entry.getKey();
		def value = entry.getValue();
		def newElement = doc.createElement(key);
		newElement.appendChild(doc.createTextNode(value));
		node.appendChild(newElement);	
	}
	return node;
}

/*
 * main
 */
import groovy.sql.Sql
def groovyUtils = new com.eviware.soapui.support.GroovyUtils(context);
def holder = groovyUtils.getXmlHolder(mockRequest.requestContent);

requestContext.responseRequestedPortsMessage = null;

def responseHholder = getResponseBase("getResp");
def req1, req4, parentnode;


parentnode = responseHholder.getDomNode( "//ns1:getDataResponse//getDataReturn");

def sql = Sql.newInstance("jdbc:oracle:thin:localhost/1521/orcl", "username", "password", "oracle.jdbc.driver.OracleDriver");
sql.eachRow(generateSQLStatementBasedOnStatus(holder))
{
	def stateid = it.stateid;
	def tempPath = null;
	
	def attMap;
	def map  = new TreeMap<String,HashMap<String,String>>();
	sql.eachRow("select * from autoResponse where STATEID = ${stateid} order by path desc")
	{
		if (tempPath != it.path)
		{
			tempPath = it.path;
			attMap = new HashMap<String,String>();
		}
		attMap.put(it.attname,it.attvalue);
		map.put(it.path,attMap);
		
	}
	if (map.size() > 0)
		createStruct(map,parentnode);
}

requestContext.responseRequestedPortsMessage = responseHholder.xml;
