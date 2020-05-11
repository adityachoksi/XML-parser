public class Star {

    private String name;
    private int birthyear;


    public Star(){
    }

    public Star(String name, int birthyear) {
        this.name = name;
        this.birthyear = birthyear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthyear() {
        return birthyear;
    }

    public void setBirthyear(int birthyear) {
        this.birthyear = birthyear;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star Details - ");
        sb.append("Name: " + getName());
        sb.append(", ");
        sb.append("Birth Year: " + getBirthyear());

        return sb.toString();
    }
}