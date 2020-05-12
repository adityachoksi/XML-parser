
import java.io.IOException;
import java.sql.*;
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

public class XMLParser implements Parameters{

    List<Movie> Movies;
    List<Star> Stars;
    List<Sim> Sims;
    Document dom;

    public XMLParser() {
        //create a list to hold the employee objects
        Movies = new ArrayList<>();
        Stars = new ArrayList<>();
        Sims = new ArrayList<>();
    }

    public void runParser() {

        //parse the xml file and get the dom object
        parseXmlFile("data/mains243.xml");

        //get each employee element and create a Employee object
        parseMovies();

        parseXmlFile("data/actors63.xml");

        parseStars();

        parseXmlFile("data/casts124.xml");

        parseCasts();

        //Iterate through the list and print the data
        printData();

    }

    private void parseXmlFile(String path) {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            //Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();

            //parse using builder to get DOM representation of the XML file
            dom = db.parse(path);

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

    private void parseMovies() {
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

    private void parseStars() {
        //get the root elememt
        Element docEle = dom.getDocumentElement();

        //get a nodelist of <directorfilms> elements
        String currentName;
        int currentBirthyear;

        Star s;

        NodeList na = docEle.getElementsByTagName("actor");
        if (na != null && na.getLength() > 0) {
            for (int i = 0; i < na.getLength(); i++) {

                //get the directorfilm element
                Element actor = (Element) na.item(i);

                currentName = getTextValue(actor,"stagename");
                currentBirthyear = getIntValue(actor,"dob");


                if (currentName!=null) {
                    s = new Star(currentName,currentBirthyear);
                    Stars.add(s);

                }
            }
        }
    }

    private void parseCasts() {
        //get the root elememt
        Element docEle = dom.getDocumentElement();

        //get a nodelist of <directorfilms> elements
        String currentMovie;
        String currentStar;

        Sim sm;

        NodeList ndf = docEle.getElementsByTagName("dirfilms");
        if (ndf != null && ndf.getLength() > 0) {
            for (int i = 0; i < ndf.getLength(); i++) {

                //get the directorfilm element
                Element dfl = (Element) ndf.item(i);

                NodeList nf = dfl.getElementsByTagName("filmc");
                if (nf != null && nf.getLength() > 0) {
                    for (int j = 0; j < nf.getLength(); j++) {

                        Element film = (Element) nf.item(j);

                        NodeList nc = film.getElementsByTagName("m");
                        if (nc!=null && nc.getLength() >0){
                            for (int k = 0; k < nc.getLength(); k++) {

                                Element cast = (Element) nc.item(k);

                                currentMovie = getTextValue(cast, "t");
                                currentStar = getTextValue(cast, "a");

                                if (currentMovie != null && currentStar != null && !(currentStar.equals("s a"))) {
                                    sm = new Sim(currentStar, currentMovie);
                                    Sims.add(sm);
                                }
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

        //Iterator<Movie> it = Movies.iterator();
        //Iterator<Sim> it = Sims.iterator();
        Iterator<Star> it = Stars.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().toString());
        }
        System.out.println("No of Sims '" + Sims.size() + "'.");
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {


        //create an instance
        XMLParser dpe = new XMLParser();

        //call run example
        dpe.runParser();

        Class.forName("com.mysql.jdbc.Driver").newInstance();


        // Connect to the test database
        try {
            Connection connection = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                    Parameters.username, Parameters.password);

            int count
            if (connection != null) {
                System.out.println("Connection established!!");
                System.out.println();

                Iterator<Star> it = dpe.Stars.iterator();
                String starname = "";
                int birthyear = -1;
                Star s;
                int result;

                // debug variables
                int failcount = 0;
                int successcount = 0;

                CallableStatement addStar = connection.prepareCall("call add_star(?,?)");
                while (it.hasNext()) {
                    s = it.next();
                    starname = s.getName();
                    birthyear = s.getBirthyear();
                    //System.out.println("name: " + starname);

                    //System.out.println("done w preparecall");
                    addStar.setString(1,starname);
                    addStar.setInt(2,birthyear);
                    //System.out.println("done w params");
                    //System.out.println(addStar.getParameterMetaData().getParameterMode(2));
                    //addStar.registerOutParameter(3,Types.INTEGER);
                    //System.out.println("done w outparam");
                    addStar.addBatch();
                    //System.out.println("done executing");
                    /*
                    result = addStar.getInt(3);
                    if(result == 1){
                        successcount++;
                        //System.out.println("SUCCESS: added star");
                    }else{
                        failcount++;
                        //System.out.println("Failure: didn't add star");
                    }*/
                }
                addStar.executeBatch();
                //System.out.println("number of SUCCESS: added star = " + successcount);
                //System.out.println("number of FAILURE: added star = " + failcount);

                System.out.println("done w stars");
                successcount = 0;
                failcount = 0;

                Iterator<Movie> it1 = dpe.Movies.iterator();
                String MovieTitle;
                int MovieYear;
                String MovieDirector;
                String MovieGenre;
                Movie m;
                CallableStatement addMovie = connection.prepareCall("call add_movie_simple(?,?,?,?)");
                while (it1.hasNext()) {
                    m = it1.next();
                    MovieTitle = m.getTitle();
                    MovieYear = m.getYear();
                    MovieDirector = m.getDirector();
                    MovieGenre = m.getGenre();
                    addMovie.setString(1,MovieTitle);
                    addMovie.setInt(2,MovieYear);
                    addMovie.setString(3,MovieDirector);
                    addMovie.setString(4,MovieGenre);
                    //addMovie.registerOutParameter(5,Types.INTEGER);
                    addMovie.addBatch();
                    /*
                    result = addMovie.getInt(5);
                    if(result == 1){
                        successcount++;
                        //System.out.println("SUCCESS: added movie");
                    }else{
                        failcount++;
                        //System.out.println("Failure: didn't add movie");
                    }*/

                }
                addMovie.executeBatch();
                System.out.println("done w movies");
                //System.out.println("number of SUCCESS: added movie = " + successcount);
                //System.out.println("number of FAILURE: added movie = " + failcount);


                successcount = 0;
                failcount = 0;

                Iterator<Sim> it2 = dpe.Sims.iterator();
                Sim sm;
                int no_som = 0;
                CallableStatement addLink = connection.prepareCall("call link_movie_star(?,?)");
                while(it2.hasNext()){
                    sm = it2.next();
                    starname = sm.getStar();
                    MovieTitle = sm.getMovie();
                    addLink.setString(1,MovieTitle);
                    addLink.setString(2,starname);
                    //addLink.registerOutParameter(3,Types.INTEGER);
                    addLink.addBatch();
                    /*
                    result = addLink.getInt(3);
                    if(result == 1){
                        successcount++;
                        //System.out.println("SUCCESS: added movie");
                    }else if(result == 1){
                        failcount++;
                        //System.out.println("Failure: didn't add movie");
                    }else{
                        no_som++;
                    }*/

                }
                addLink.executeBatch();
                System.out.println("done w link");
            //    System.out.println("number of SUCCESS: added link = " + successcount);
            //    System.out.println("number of FAILURE: added link = " + failcount);
            //    System.out.println("number of FAILURE (no star or movie): added link = " + no_som);




            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println("sql Error");
            int j = 6;
        }


    }

}
