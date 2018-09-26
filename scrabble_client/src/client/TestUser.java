package client;

public class TestUser {
    private String username;
    private String userid;
    private String message;
    public TestUser(String username,String userid,String message){
        this.username = username;
        this.userid = userid;
        this.message = message;
    }
    public String getUsername(){
        return username;
    }
    public String getUserid(){
        return userid;
    }
    public String getMessage(){
        return  message;
    }
    public void setMessage(){
        this.message = message;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setUserid(String userid){
        this.userid = userid;
    }
}
