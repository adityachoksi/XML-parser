
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.*;

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

    public void runParser(PrintWriter writer) {

        //parse the xml file and get the dom object
        parseXmlFile("data/mains243.xml");

        //get each employee element and create a Employee object
        parseMovies(writer);

        parseXmlFile("data/actors63.xml");

        parseStars(writer);

        parseXmlFile("data/casts124.xml");

        parseCasts(writer);

        //Iterate through the list and print the data
        //printData();

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

    private void parseMovies(PrintWriter writer) {
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
                //System.out.println(currentDirector);

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
                            else{
                                    writer.println("Inconsistent Movie; Title: " + currentTitle + ", Year: " + currentYear
                                            + ", Director: " + currentDirector + ",Genre: " + currentGenre);
                            }
                        }
                    }
                }
            }
        }
    }

    private void parseStars(PrintWriter writer) {
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
                    s = new Star(currentName, currentBirthyear);
                    Stars.add(s);
                }
                else{
                    writer.println("Inconsistent Star; Name: " + currentName + ", DOB: " + currentBirthyear);
                }
            }
        }
    }

    private void parseCasts(PrintWriter writer) {
        //get the root elememt
        Element docEle = dom.getDocumentElement();

        //get a nodelist of <directorfilms> elements
        String currentMovie;
        String currentDirector;
        String currentStar;

        Sim sm;

        NodeList ndf = docEle.getElementsByTagName("dirfilms");
        if (ndf != null && ndf.getLength() > 0) {
            for (int i = 0; i < ndf.getLength(); i++) {

                //get the directorfilm element
                Element dfl = (Element) ndf.item(i);
                currentDirector = getTextValue(dfl,"is");

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
                                    sm = new Sim(currentStar, currentMovie,currentDirector);
                                    Sims.add(sm);
                                }
                                else{
                                    writer.println("Inconsistent SIM; Star: " + currentStar + ", Movie: " + currentMovie);
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

    private static HashSet<String> getStarSet(Connection conn){
        HashSet<String> starSet = new HashSet<String>();
        try {
            Statement select = conn.createStatement();
            String query = "SELECT name from stars;";

            ResultSet rs = select.executeQuery(query);
            while (rs.next()) {
                starSet.add(rs.getString("name"));
            }
        }
        catch(Exception e){
            System.out.print("getStarset DB Error");
        }
        return starSet;
    }

    private static HashSet<String> getMovieMap(Connection conn){
        HashSet<String> MovieSet = new HashSet<String>();
        try {
            Statement select = conn.createStatement();
            String query = "SELECT title,year,director from movies;";

            ResultSet rs = select.executeQuery(query);
            while (rs.next()) {
                String mconcat = rs.getString("title") + rs.getString("year") + rs.getString("director");
                MovieSet.add(mconcat.toLowerCase());
            }
        }
        catch(Exception e){
            System.out.print("getMovieMap DB Error");
        }
        return MovieSet;
    }

    private static HashMap<String,String> getStarIdMap(Connection conn){
        HashMap<String,String> StarMap = new HashMap<String,String>();
        try {
            Statement select = conn.createStatement();
            String query = "SELECT id,name from stars;";

            ResultSet rs = select.executeQuery(query);
            while (rs.next()) {
                StarMap.put(rs.getString("name"),rs.getString("id"));
            }
        }
        catch(Exception e){
            System.out.print("getStarIDMap DB Error");
        }
        return StarMap;
    }


    private static HashMap<String,String> getMovieIdMap(Connection conn){
        HashMap<String,String> MovieMap = new HashMap<String,String>();
        try {
            Statement select = conn.createStatement();
            String query = "SELECT id,title from movies;";

            ResultSet rs = select.executeQuery(query);
            while (rs.next()) {
                MovieMap.put(rs.getString("title"),rs.getString("id"));
            }
        }
        catch(Exception e){
            System.out.print("getMovieIDMap DB Error");
        }
        return MovieMap;
    }

    private static HashSet<String> getSimSet(Connection conn){
        HashSet<String> SimSet = new HashSet<String>();
        try {
            Statement select = conn.createStatement();
            String query = "SELECT starId,movieId from stars_in_movies;";

            ResultSet rs = select.executeQuery(query);
            while (rs.next()) {
                SimSet.add(rs.getString("starId")+rs.getString("movieId"));
            }
        }
        catch(Exception e){
            e.getMessage();
        }
        return SimSet;
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, FileNotFoundException, UnsupportedEncodingException {

        PrintWriter writer = new PrintWriter("errorreport.txt", "UTF-8");
        //create an instance
        XMLParser dpe = new XMLParser();

        //call run example
        dpe.runParser(writer);

        Class.forName("com.mysql.jdbc.Driver").newInstance();


        // Connect to the test database
        try {
            Connection connection = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" + Parameters.dbname + "?autoReconnect=true&useSSL=false",
                    Parameters.username, Parameters.password);

            if (connection != null) {
                System.out.println("Connection established!!");
                System.out.println();


                int batch_count =0;


                String starname = "";
                int birthyear = -1;

                int duplicates = 0;
                int total = 0;
                Star s;
                HashSet<String> existingStars = getStarSet(connection);
                Iterator<Star> it = dpe.Stars.iterator();
                CallableStatement addStar = connection.prepareCall("call add_star(?,?)");
                while (it.hasNext()) {
                    s=it.next();
                    if (existingStars.contains(s.getName())) {
                        writer.println("Duplicate Star: " + s.toString());
                        duplicates++;
                    }else{
                        starname = s.getName();
                        birthyear = s.getBirthyear();
                        addStar.setString(1,starname);
                        addStar.setInt(2,birthyear);
                        addStar.addBatch();
                        batch_count++;
                        if(batch_count % 1000 == 0){
                            addStar.executeBatch();
                            System.out.println("doing batch: " + batch_count);
                        }
                    }
                    total++;
                }


                //Iterator<Star> it = dpe.Stars.iterator();

                //Star s;
                int result;

                // debug variables
                int failcount = 0;
                int successcount = 0;

                if(batch_count%1000 !=0)addStar.executeBatch();
                batch_count = 0;
                //System.out.println("number of SUCCESS: added star = " + successcount);
                //System.out.println("number of FAILURE: added star = " + failcount);

                System.out.println("done w stars");

                HashMap<String,String> StarIds = getStarIdMap(connection);
                System.out.println("starsIds len = " + StarIds.size());

                successcount = 0;
                failcount = 0;



                Iterator<Movie> it1 = dpe.Movies.iterator();
                String MovieTitle;
                int MovieYear;
                String MovieDirector;
                String MovieGenre;
                Movie m;

                duplicates = 0;
                total=0;
                String mconcat;
                HashSet<String> existingMovies = getMovieMap(connection);

                CallableStatement addMovie = connection.prepareCall("call add_movie_simple(?,?,?,?)");
                while (it1.hasNext()) {
                    m = it1.next();
                    MovieTitle = m.getTitle();
                    MovieYear = m.getYear();
                    MovieDirector = m.getDirector();
                    MovieGenre = m.getGenre();
                    mconcat = MovieTitle+MovieYear+MovieDirector;
                    if (existingMovies.contains(mconcat.toLowerCase())) {
                        writer.println("Duplicate Movie" + m.toString());
                        duplicates++;
                    }else {
                        addMovie.setString(1, MovieTitle);
                        addMovie.setInt(2, MovieYear);
                        addMovie.setString(3, MovieDirector);
                        addMovie.setString(4, MovieGenre);
                        //addMovie.registerOutParameter(5,Types.INTEGER);
                        addMovie.addBatch();
                        batch_count++;
                        if (batch_count % 1000 == 0) {
                            addMovie.executeBatch();
                            System.out.println("doing batch: " + batch_count);
                        }
                    }
                    total++;
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
                if(batch_count%1000==0)addMovie.executeBatch();
                batch_count = 0;
                System.out.println("done w movies");

                HashMap<String,String> MovieIds = getMovieIdMap(connection);
                System.out.println("MovieIds len = " + MovieIds.size());
                //System.out.println("number of SUCCESS: added movie = " + successcount);
                //System.out.println("number of FAILURE: added movie = " + failcount);


                successcount = 0;
                failcount = 0;

                HashSet<String> SimSet = getSimSet(connection);

                Iterator<Sim> it2 = dpe.Sims.iterator();
                Sim sm;
                int no_som = 0;

                String starId;
                String movieId;

                CallableStatement addLink = connection.prepareCall("call link_movie_star(?,?)");
                while(it2.hasNext()){
                    sm = it2.next();
                    starname = sm.getStar();
                    MovieTitle = sm.getMovie();
                    starId = StarIds.get(starname);
                    movieId = MovieIds.get(MovieTitle);
                    //System.out.println("title: " + MovieTitle + "\tid: " + movieId);
                    if(starId == null || movieId == null){
                        writer.println("Error: null value; starId=" + starId + " movieId=" + movieId);
                    }else if (existingMovies.contains(starId+movieId)){
                        writer.println("Duplicate SIM Entry: " + starId+ " " +movieId);
                        duplicates++;
                    }else {
                        addLink.setString(1, movieId);
                        addLink.setString(2, starId);
                        //addLink.registerOutParameter(3,Types.INTEGER);
                        addLink.addBatch();
                        batch_count++;
                        if (batch_count % 1000 == 0) {
                            addLink.executeBatch();
                            System.out.println("doing batch: " + batch_count);
                        }
                    }


                }
                if(batch_count %1000 == 0){
                    addLink.executeBatch();
                }

                batch_count = 0;
                System.out.println("done w link");
                //    System.out.println("number of SUCCESS: added link = " + successcount);
                //    System.out.println("number of FAILURE: added link = " + failcount);
                //    System.out.println("number of FAILURE (no star or movie): added link = " + no_som);




            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }

        writer.close();


    }

}