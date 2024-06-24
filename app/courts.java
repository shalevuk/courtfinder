public class courts {
    private String name;
    private String address;
    private int imageResId; // זהו מזהה עבור תמונה, ניתן לשנות את זה ל-String אם משתמשים במזהה תמונה מקויים ברשת או אחרת

    public courts(String name, String address, int imageResId) {
        this.name = name;
        this.address = address;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
// Getters and setters

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

