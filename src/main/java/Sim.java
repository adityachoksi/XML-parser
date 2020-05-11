public class Sim {

    private String star;
    private String movie;


    public Sim(){
    }

    public Sim(String star, String movie) {
        this.star = star;
        this.movie = movie;
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


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star Details - ");
        sb.append("Star: " + getStar());
        sb.append(", ");
        sb.append("Movie: " + getMovie());

        return sb.toString();
    }
}