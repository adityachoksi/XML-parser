public class Sim {

    private String star;
    private String movie;
    private String director;


    public Sim(){
    }

    public Sim(String star, String movie,String director) {
        this.star = star;
        this.movie = movie;
        this.director = director;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getMovie() { return movie; }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Sim Details - ");
        sb.append("Star: " + getStar());
        sb.append(", ");
        sb.append("Movie: " + getMovie());
        sb.append(", ");
        sb.append("Director: " + getDirector());

        return sb.toString();
    }
}