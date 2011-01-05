/***LICENSE START
 * Copyright 2011 EUROPEAN UNION
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * 
 * Date: 03 January 2011
 * Authors: Marzia Grasso, Angelo Quaglia, Massimo Craglia
LICENSE END***/

package eu.europa.ec.jrc.euosme.gwt.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import eu.europa.ec.jrc.euosme.gwt.client.RESTfulWebServiceException;
import eu.europa.ec.jrc.euosme.gwt.client.RESTfulWebServiceProxy;
import eu.inspire.geoportal.viewclient.cache.gemet.Concept;
import eu.inspire.geoportal.viewclient.cache.gemet.GemetClient;

public class RESTfulWebServiceProxyImpl extends RemoteServiceServlet implements RESTfulWebServiceProxy {
	private static final long serialVersionUID = 1L;
	static String kuser = ""; // your account name
	static String kpass = ""; // your password for the account
	static String codelists = ""; 
	static String repositories = "";
	static String limit = "200";
	static String dataThemes = "";
	static Boolean saveCodeList=false;
	static String inspireValidationService = "";
	static String inspireWebService = "";
	
    static class MyAuthenticator extends Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            return (new PasswordAuthentication(kuser, kpass.toCharArray()));
        }
    }

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        kuser = config.getInitParameter("username");
        kpass = config.getInitParameter("password"); 
        codelists = config.getInitParameter("codelists");
        repositories = config.getInitParameter("repositories");
        dataThemes = config.getInitParameter("dataThemes");
        limit = config.getInitParameter("limit");
        inspireValidationService = config.getInitParameter("inspireValidationService");
        inspireWebService = config.getInitParameter("inspireWebService");
      }
    
    public RESTfulWebServiceProxyImpl() { // must have
    }

    public String invokeGetRESTfulWebService(String paramName, String extraValue, String clientLanguage, String filter) 
    	throws RESTfulWebServiceException {
    	try {
    		//check user and password for semantic researches
    		if (!paramName.equalsIgnoreCase("codelists") && kuser.isEmpty())
    			return "AUTHENTICATIONFAILED";
    		
        	String urlParameters = "";
        	String uri = "";
        	String encoding = "UTF-8";
        	if (paramName.equalsIgnoreCase("codelists")) {
        		uri = codelists + extraValue + "/values?max=" + limit;
        	}
        	else {
        		if (paramName.equalsIgnoreCase("repositories")) {
        			uri = repositories + "?queryLn=SPARQL&query=PREFIX%20rdfs%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0APREFIX%20owl2xml%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2006%2F12%2Fowl2-xml%23%3E%0APREFIX%20dct%3A%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Fterms%2F%3E%0APREFIX%20xsd%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema%23%3E%0APREFIX%20owl%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2002%2F07%2Fowl%23%3E%0APREFIX%20rdf%3A%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0APREFIX%20inspire%3A%3Chttp%3A%2F%2Finspire-registry.jrc.ec.europa.eu%2Frdfschema%2Finspire-schema.rdf%23%3E%0APREFIX%20skos%3A%3Chttp%3A%2F%2Fwww.w3.org%2F2004%2F02%2Fskos%2Fcore%23%3E%0A%0ASELECT%20DISTINCT%20%3Fv%20%3Fl%0AWHERE%20{%0A%20%20{%0A%20%20%20%20%3Fv%20skos%3AprefLabel%20%3Fl.%0A%20%20%20%20%3Fv%20rdf%3Atype%20skos%3AConceptScheme.%0A%20%20}%0A}%0AORDER%20BY%20ASC%28%3Fl%29&limit=" + limit + "&infer=true";
        		}
        		if (paramName.equalsIgnoreCase("narrower")) {
    				uri = repositories;
    				urlParameters="queryLn=" + URLEncoder.encode("SPARQL", "UTF-8");
    				String query="PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
					"PREFIX owl2xml:<http://www.w3.org/2006/12/owl2-xml#>\n" +
					"PREFIX dct:<http://purl.org/dc/terms/>\n" +
					"PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>\n" +
					"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n" +
					"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					"PREFIX inspire:<http://inspire-registry.jrc.ec.europa.eu/rdfschema/inspire-schema.rdf#>\n" +
					"PREFIX skos:<http://www.w3.org/2004/02/skos/core#>\n" +
					"\n" +
					"SELECT DISTINCT ?c ?l ?a ?d\n" +
					"WHERE {\n" +
					"  {\n" +
					"    ?c skos:prefLabel ?l.\n" +
					"    ?c skos:broader <" + extraValue + ">.\n" +
					"    OPTIONAL {\n" +
					"      ?c skos:narrower ?n.\n" +
					"      ?n skos:inScheme ?d.\n" +
					"    }\n" +
					"    OPTIONAL {\n" +
					"      ?c skos:prefLabel ?a.\n" +
					"      FILTER ( LANG(?a) = \"" + clientLanguage + "\" )\n" +
					"    }\n" +
					"    FILTER ( LANG(?l) = \"en\")\n" +
					"  }\n" +
					"  UNION\n" +
					"  {\n" +
					"    ?c skos:prefLabel ?l.\n" +
					"    <" + extraValue + "> skos:member ?c.\n" +
					"    OPTIONAL {\n" +
					"      ?c skos:narrower ?n.\n" +
					"      ?n skos:inScheme ?d.\n" +
					"    }\n" +
					"    OPTIONAL {\n" +
					"      ?c skos:prefLabel ?a.\n" +
					"      FILTER ( LANG(?a) = \"" + clientLanguage + "\" )\n" +
					"    }\n" +
					"    FILTER ( LANG(?l) = \"en\")\n" +
					"  }\n" +
					"  UNION\n" +
					"  {\n" +
					"    ?c rdfs:label ?l.\n" +
					"    ?c skos:broader <" + extraValue + ">.\n" +
					"    OPTIONAL {\n" +
					"      ?c skos:narrower ?n.\n" +
					"      ?n skos:inScheme ?d.\n" +
					"    }\n" +
					"    OPTIONAL {\n" +
					"      ?c skos:member ?n.\n" +
					"      ?n skos:inScheme ?d.\n" +
					"    }\n" +
					"    OPTIONAL {\n" +
					"      ?c rdfs:label ?a.\n" +
					"      FILTER ( LANG(?a) = \"" + clientLanguage + "\" )\n" +
					"    }\n" +
					"    FILTER ( LANG(?l) = \"en\")\n" +
					"  }\n" +
					"}\n";
					urlParameters+="&query=" + URLEncoder.encode(query, "UTF-8");
    				urlParameters+="&limit=" + limit + "&infer=true";    				
        		}    		
        		if (paramName.equalsIgnoreCase("repository")) {
        				uri = repositories;
        				urlParameters="queryLn=" + URLEncoder.encode("SPARQL", "UTF-8");
        				String query="PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
        				"PREFIX owl2xml:<http://www.w3.org/2006/12/owl2-xml#>\n" +
        				"PREFIX dct:<http://purl.org/dc/terms/>\n" +
        				"PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>\n" +
        				"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n" +
        				"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
        				"PREFIX inspire:<http://inspire-registry.jrc.ec.europa.eu/rdfschema/inspire-schema.rdf#>\n" +
        				"PREFIX skos:<http://www.w3.org/2004/02/skos/core#>\n" +
        				"\n" +
        				"SELECT DISTINCT ?c ?l ?a ?d\n"+
        				"WHERE {\n"+
        				"  {\n" +
        				"    ?c skos:prefLabel ?l." +
        				"    ?c skos:inScheme <" + extraValue + ">.\n"+
        				"    OPTIONAL {\n"+
        				"      ?c skos:broader ?b.\n" +
        				"    }\n" +
        				"    OPTIONAL {\n"+
        				"    ?c skos:narrower ?n.\n"+
        				"    ?n skos:inScheme ?d.\n"+
        				"    }\n"+
        				"    OPTIONAL {\n"+
        				"      ?c skos:prefLabel ?a.\n"+
        				"      FILTER ( LANG(?a) = \"" + clientLanguage + "\" )\n"+
        				"    }\n"+
        				"    FILTER ( LANG(?l) = \"en\" && !BOUND(?b) )\n";
        				if (!filter.isEmpty()) query+="    FILTER (regex(str(?l),\"^" + filter + "\",\"i\"))\n";
        				query+="  }\n"+
        				"  UNION\n"+
        				"  {\n"+
        				"    ?c rdfs:label ?l.\n"+
        				"    ?c skos:inScheme <" + extraValue + ">.\n"+
        				"    OPTIONAL {\n"+
        				"      ?c skos:broader ?b.\n"+
        				"    }\n"+
        				"    OPTIONAL {\n"+
        				"      ?c skos:narrower ?n.\n"+
        				"      ?n skos:inScheme ?d.\n"+
        				"    }\n"+
        				"    OPTIONAL {\n"+
        				"      ?c skos:member ?n.\n"+
        				"      ?n skos:inScheme ?d.\n"+
        				"    }\n"+
        				"    OPTIONAL {\n"+
        				"      ?c rdfs:label ?a.\n"+
        				"      FILTER ( LANG(?a) = \"" + clientLanguage + "\" )\n"+
        				"    }\n"+
        				"    FILTER ( LANG(?l) = \"en\" && !BOUND(?b) )\n";
        				if (!filter.isEmpty()) query+="    FILTER (regex(str(?l),\"^" + filter + "\",\"i\"))\n";
        				query+="  }\n"+
        				"}\n";
        				urlParameters+="&query=" + URLEncoder.encode(query, "UTF-8");
        				urlParameters+="&limit=" + limit + "&infer=true";        				
        		}
        		Authenticator.setDefault(new MyAuthenticator());
        	}        	
            URL u = new URL(uri);
            HttpURLConnection uc = (HttpURLConnection) u.openConnection();
            if (paramName.equalsIgnoreCase("repository") || paramName.equalsIgnoreCase("narrower")) {
            	uc.setRequestMethod("POST");
                uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
                uc.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
                uc.setRequestProperty("Content-Language", clientLanguage );
                uc.setUseCaches (false);
                uc.setDoInput(true);
                uc.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream (uc.getOutputStream ());
                wr.writeBytes (urlParameters);
                wr.flush ();
                wr.close ();
                if (uc.getContentEncoding()!=null) encoding = uc.getContentEncoding();
            }    
            if (paramName.equalsIgnoreCase("repositories")) {
            	uc.setRequestProperty("Content-Type", "application/xml;charset=ISO-8859-1");
            	uc.setRequestMethod("GET");
            	uc.setUseCaches(true);
            	int status = uc.getResponseCode();
                if (status != 200)
                	if (status == 401) return "AUTHENTICATIONFAILED";
                	else throw (new RESTfulWebServiceException("Invalid HTTP response status code " + status + " from web service server."));
                if (uc.getContentEncoding()!=null) encoding = uc.getContentEncoding();
                //else encoding = "ISO-8859-1";
              
            }           	
            if (paramName.equalsIgnoreCase("codelists")) {            	
            	uc.setDoOutput(false);
            	uc.setRequestProperty("accept", "application/json");
            	uc.setRequestProperty("accept-language", clientLanguage);
            	uc.setRequestMethod("GET");
            	uc.setUseCaches(true);
            	int status = uc.getResponseCode();
                if (status != 200)
                    throw (new RESTfulWebServiceException("Invalid HTTP response status code " + status + " from web service server."));
                if (uc.getContentEncoding()!=null) encoding = uc.getContentEncoding();
              
            }                                  
            BufferedReader d = new BufferedReader(new InputStreamReader(uc.getInputStream(), encoding));
            StringBuilder buffer = new StringBuilder(16384);
            try {
            	String line;
                while ((line = d.readLine()) != null) {
                	buffer.append(line.trim());                	
                }
            } finally {
            	d.close();
	        }
            if (paramName.equalsIgnoreCase("codelists") && saveCodeList) {         
	            ServletContext context = getServletConfig().getServletContext();
	    		String dir = "";
				if (context.getRealPath("temp")==null) dir = context.getRealPath("/euosme/temp");
	    		else dir = context.getRealPath("temp");
	    		try {
	    			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + "/" + extraValue + "_" + clientLanguage + ".txt" ), "UTF-8"));
	             	out.append(buffer.toString());
	            	out.flush();
	           		out.close();
	    		}
	           	catch (IOException e)    {   
	        	   	e.printStackTrace();
	        	}	
            }
            return buffer.toString();
            
        } 
        catch (MalformedURLException e) {
        	throw new RESTfulWebServiceException(e.getMessage(), e);
        }
        catch (IOException e) {
            throw new RESTfulWebServiceException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
	public Map<String,String> invokeGemetService(String clientLanguage) 
	throws RESTfulWebServiceException {
    	try {
    		Map<String,String> definitions = new LinkedHashMap<String, String>();
    		GemetClient instance = new GemetClient(dataThemes);    		
    		ArrayList c = instance.getTopmostConcepts(instance.getThesaurusInspireThemes(),new Locale(clientLanguage));
    		Iterator i = c.iterator();
            while (i.hasNext()) {
                Concept concept = (Concept) i.next();
                String preferredLabel = concept.getPreferredLabel().getPropertyValue().getString();
                String definition = concept.getDefinition().getPropertyValue().getString();
                @SuppressWarnings("unused")
				String definition_uri = concept.getUri();
                definitions.put(preferredLabel,definition);
            }
        	return definitions;        
	    } 
	    catch (MalformedURLException e) {
	    	throw new RESTfulWebServiceException(e.getMessage(), e);
	    }	   
	 }
    
	public String invokeUpdateRESTfulWebService() 
	throws RESTfulWebServiceException {
		try {
	    	String uri = "";
	    	String encoding = "UTF-8";
	    	ServletContext context = getServletConfig().getServletContext();
			String dir = "";
			if (context.getRealPath("temp")==null) dir = context.getRealPath("/euosme/temp");
			else dir = context.getRealPath("temp");
	    	for (int extraValue=2;extraValue<=11;extraValue++) {
	    		uri = codelists + extraValue + "/values?max=" + limit;
	    		URL u = new URL(uri);
	    		String[] languages={"bg","cs","da","de","el","en","es","et","fi","fr","hu","it","lt","lv","mt","nl","pl","pt","ro","sk","sl","sv"};
	    		for (int i = 0; i<languages.length; i++ ) {
	    			String clientLanguage = languages[i];
		            HttpURLConnection uc = (HttpURLConnection) u.openConnection();
		            uc.setDoOutput(false);
		        	uc.setRequestProperty("accept", "application/json");
		        	uc.setRequestProperty("accept-language", clientLanguage);
		        	uc.setRequestMethod("GET");
		        	uc.setUseCaches(true);
		        	int status = uc.getResponseCode();
		            if (status != 200)
		                throw (new RESTfulWebServiceException("Invalid HTTP response status code " + status + " from web service server."));
		            if (uc.getContentEncoding()!=null) encoding = uc.getContentEncoding();            
			        BufferedReader d = new BufferedReader(new InputStreamReader(uc.getInputStream(), encoding));
			        StringBuilder buffer = new StringBuilder(16384);
			        try {
			        	String line;
			            while ((line = d.readLine()) != null) {
			            	buffer.append(line.trim());                	
			            }
			        } finally {
			        	d.close();
			        }		        
		    		try {
		    			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + "/" + extraValue + "_" + clientLanguage + ".txt" ), "UTF-8"));
		             	out.append(buffer.toString());
		            	out.flush();
		           		out.close();
		           		if (clientLanguage.equalsIgnoreCase("en")) {
		           			Writer outOriginal = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + "/" + extraValue + ".txt" ), "UTF-8"));
		           			outOriginal.append(buffer.toString());
		           			outOriginal.flush();
		           			outOriginal.close();
		           		}
		    		}
		           	catch (IOException e)    {   
		        	   	e.printStackTrace();
		        	}
	    		}
	    	}
	    } 
	    catch (MalformedURLException e) {
	    	throw new RESTfulWebServiceException(e.getMessage(), e);
	    }
	    catch (IOException e) {
	        throw new RESTfulWebServiceException(e.getMessage(), e);
	    }
		return null;
	}
	
	public String invokeValidationService(String XMLTree) 
 	throws RESTfulWebServiceException {
		String dir = "";
		try {
	    	ServletContext context = getServletConfig().getServletContext();			
			if (context.getRealPath("temp")==null) dir = context.getRealPath("/euosme/temp");
			else dir = context.getRealPath("temp");
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + "/tmp.xml" ), "UTF-8"));
         	out.append(XMLTree);
        	out.flush();
       		out.close();
	    } 
	    catch (MalformedURLException e) {
	    	throw new RESTfulWebServiceException(e.getMessage(), e);
	    }
	    catch (IOException e) {
	        throw new RESTfulWebServiceException(e.getMessage(), e);
	    }
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String exsistingFileName = dir + "/tmp.xml";
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;
		
		try {		   //------------------ CLIENT REQUEST
			FileInputStream fileInputStream = new FileInputStream( new File(exsistingFileName) );
			// open a URL connection to the Servlet 
			URL url = new URL(inspireValidationService);
			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			// Allow Inputs
			conn.setDoInput(true);
			// Allow Outputs
			conn.setDoOutput(true);
			// Don't use a cached copy.
			conn.setUseCaches(false);
			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
			dos = new DataOutputStream( conn.getOutputStream() );
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"dataFile\";"  + " filename=\"tmp.xml" + lineEnd);
			dos.writeBytes(lineEnd);
			// create a buffer of maximum size
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];
			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			// close streams
			fileInputStream.close();
			dos.flush();
			dos.close();
			
			BufferedReader d = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
		    StringBuilder bufferResponse = new StringBuilder(16384);
		    try {
		    	String lineResponse;
				while ((lineResponse = d.readLine()) != null) {
		    		bufferResponse.append(lineResponse.trim());                	
		        }
		    } finally {
		      	d.close();
			}
		    // Delete temporary file
		    File f = new File(exsistingFileName); // A File object to represent the filename
		    f.delete(); // Attempt to delete it
		    // Return message
		    return bufferResponse.toString();
		}
		catch (MalformedURLException ex) {
			System.out.println("From ServletCom CLIENT REQUEST:"+ex);
		}
		catch (IOException ioe) {
			System.out.println("From ServletCom CLIENT REQUEST:"+ioe);
		}
		return null;
	}
	
	public String invokeInspireMetadataConverterService(String XMLTree, String clientLanguage) 
 	throws RESTfulWebServiceException {
		try {
            URL u = new URL(inspireWebService + "resources/INSPIREResource");
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/xml;charset=UTF-8");
            urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(XMLTree.getBytes().length));
            urlConnection.setRequestProperty("Accept-language", clientLanguage );
            urlConnection.setUseCaches (false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream (urlConnection.getOutputStream ());
            wr.writeBytes (XMLTree);
            wr.flush ();
            wr.close ();
            int status = urlConnection.getResponseCode();
            if (status != 200)
                throw (new RESTfulWebServiceException("Invalid HTTP response status code " + status + " from web service server."));
           
            BufferedReader d = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
	        StringBuilder buffer = new StringBuilder(16384);
	        try {
	        	String line;
	            while ((line = d.readLine()) != null) {
	            	buffer.append(line.trim());                	
	            }
	        } finally {
	        	d.close();
	        }		 
            return buffer.toString();
       } 
       catch (MalformedURLException e) {
       	throw new RESTfulWebServiceException(e.getMessage(), e);
       }
       catch (IOException e) {
           throw new RESTfulWebServiceException(e.getMessage(), e);
       }		
	}
	
	public String invokeInspireUUIDService() 
 	throws RESTfulWebServiceException {
		try {
            URL u = new URL(inspireWebService + "resources/UUID ");
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("accept","application/xml");
            urlConnection.setUseCaches (false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            int status = urlConnection.getResponseCode();
            if (status != 200)
                throw (new RESTfulWebServiceException("Invalid HTTP response status code " + status + " from web service server."));
           
            BufferedReader d = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
	        StringBuilder buffer = new StringBuilder(16384);
	        try {
	        	String line;
	            while ((line = d.readLine()) != null) {
	            	buffer.append(line.trim());                	
	            }
	        } finally {
	        	d.close();
	        }		 
            return buffer.toString();
       } 
       catch (MalformedURLException e) {
       	throw new RESTfulWebServiceException(e.getMessage(), e);
       }
       catch (IOException e) {
           throw new RESTfulWebServiceException(e.getMessage(), e);
       }		
	}
}                