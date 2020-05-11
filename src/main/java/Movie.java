public class Movie {

    private String title;
    private int year;
    private String director;
    private String genre;

    public Movie(){
    }

    public Movie(String title, int year, String director,String genre) {
        this.title = title;
        this.year = year;
        this.director  = director;
        this.genre = genre;

    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }


    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("Title:" + getTitle());
        sb.append(", ");
        sb.append("Year:" + getYear());
        sb.append(", ");
        sb.append("Director:" + getDirector());
        sb.append(", ");
        sb.append("Genre:" + getGenre());
        sb.append(".");

        return sb.toString();
    }
}
