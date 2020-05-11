
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public class XMLParser implements Parameters{

    List<Movie> Movies;
    Document dom;

    public XMLParser() {
        //create a list to hold the employee objects
        Movies = new ArrayList<>();
    }

    public void runExample() {

        //parse the xml file and get the dom object
        parseXmlFile();

        //get each employee element and create a Employee object
        parseDocument();

        //Iterate through the list and print the data
        printData();

    }

    private void parseXmlFile() {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse("data/mains243.xml");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private HashMap<String,String> getCodes(){

        HashMap<String,String> genreCodes = new HashMap<String,String>();
        genreCodes.put("Susp","Thriller");
        genreCodes.put("CnR","Crime");
        genreCodes.put("Dram","Drama");
        genreCodes.put("West","Western");
        genreCodes.put("Myst","Mystery");
        genreCodes.put("S.F.","Sci-Fi");
        genreCodes.put("Advt","Adventure");
        genreCodes.put("Horr","Horror");
        genreCodes.put("Romt","Romance");
        genreCodes.put("Comd","Comedy");
        genreCodes.put("Musc","Musical");
        genreCodes.put("Docu","Documentary");
        genreCodes.put("Porn","Adult");
        genreCodes.put("Noir","Black");
        genreCodes.put("BioP","Biography");
        genreCodes.put("TV","TV Show");
        genreCodes.put("TVs","TV Series");
        genreCodes.put("TVm","TV Miniseries");
        genreCodes.put(null,null);

        return genreCodes;
    }

    private void parseDocument() {
        HashMap<String,String> genreCodes = getCodes();

        //get the root elememt
        Element docEle = dom.getDocumentElement();

        //get a nodelist of <directorfilms> elements
        String currentDirector;
        String currentTitle;
        int currentYear;
        String currentGenre;

        Movie m;

        NodeList ndf = docEle.getElementsByTagName("directorfilms");
        if (ndf != null && ndf.getLength() > 0) {
            for (int i = 0; i < ndf.getLength(); i++) {

                //get the directorfilm element
                Element dfl = (Element) ndf.item(i);

                Node dirNode = dfl.getFirstChild();
                Element director = (Element) dirNode;
                currentDirector = getTextValue(director,"dirname");
                System.out.println(currentDirector);

                Node filmsNode = dfl.getLastChild();
                Element films = (Element) filmsNode;

                NodeList nf = films.getElementsByTagName("film");
                if (nf != null && nf.getLength() > 0) {
                    for (int j = 0; j < nf.getLength(); j++) {
                        Element film = (Element) nf.item(j);
                        currentTitle = getTextValue(film,"t");
                        currentYear = getIntValue(film,"year");

                        NodeList cats = film.getElementsByTagName("cats");
                        if (cats!=null){
                            currentGenre = genreCodes.get(getTextValue((Element) cats.item(0),"cat"));
                            if (currentGenre!=null && currentTitle!=null && currentDirector!=null && currentYear!=-1) {
                                m = new Movie(currentTitle, currentYear, currentDirector, currentGenre);
                                Movies.add(m);
                            }
                        }
                    }
                }


            }
        }
    }


    /**
     * I take a xml element and the tag name, look for the tag and get
     * the text content
     * i.e for <employee><name>John</name></employee> xml snippet if
     * the Element points to employee node and tagName is name I will return John
     * 
     * @param ele
     * @param tagName
     * @return
     */
    private String getTextValue(Element ele, String tagName) {
        try {
            String textVal = null;
            NodeList nl = ele.getElementsByTagName(tagName);
            if (nl != null && nl.getLength() > 0) {
                Element el = (Element) nl.item(0);
                textVal = el.getFirstChild().getNodeValue();
            }

            return textVal;
        }
        catch (Exception e){
            return "null";
        }
    }

    /**
     * Calls getTextValue and returns a int value
     * 
     * @param ele
     * @param tagName
     * @return
     */
    private int getIntValue(Element ele, String tagName) {
        try {
            return Integer.parseInt(getTextValue(ele, tagName));
        }
        catch(Exception e) {
            return -1;
        }
    }

    /**
     * Iterate through the list and print the
     * content to console
     */
    private void printData() {

        System.out.println("No of Employees '" + Movies.size() + "'.");

        Iterator<Movie> it = Movies.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
    }

    public static void main(String[] args) {

        Connection conn = null;
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String jdbcURL="jdbc:mysql://localhost:3306/<DB NAME>";


        // Connect to the test database
        try {
            Connection connection = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                    Parameters.username, Parameters.password);

            if (connection != null) {
                System.out.println("Connection established!!");
                System.out.println();
            }
        }
        catch(Exception e){
            System.out.println("sql Error");
        }

        //create an instance
        XMLParser dpe = new XMLParser();

        //call run example
        dpe.runExample();
    }

}
