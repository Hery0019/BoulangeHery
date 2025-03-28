import dao.User;

public class Main {

    public static void main(String[] args) throws Exception {
        User user = new User("Hery", "RAKOTONARIVO", "herakotonarivo@gmail.com", "123");
        user.create();
    }
    
}