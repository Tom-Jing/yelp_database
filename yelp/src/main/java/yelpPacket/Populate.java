/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yelpPacket;

/**
 *
 * @author Tom
 */
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Iterator;

        
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;  
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Populate {
    String line = null;
    PreparedStatement businessSQL = null;
    static Connection con = null;

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException, ParseException {
        System.out.println("Connecting to DB...... ");
        DBConnect db = new DBConnect();
        con = db.openConnection();
        System.out.println("Cleaning tables...........");
        PreparedStatement emptyUser = con.prepareStatement("TRUNCATE TABLE yelp_users");
        emptyUser.executeUpdate();
        PreparedStatement emptyBus = con.prepareStatement("TRUNCATE TABLE BUSINESS");
        emptyBus.executeUpdate();
        PreparedStatement emptyRev = con.prepareStatement("TRUNCATE TABLE REVIEWS");
        emptyRev.executeUpdate();
        PreparedStatement emptyFrid = con.prepareStatement("TRUNCATE TABLE FRIENDS");
        emptyFrid.executeUpdate();
        PreparedStatement emptyVOTES = con.prepareStatement("TRUNCATE TABLE VOTES_USER");
        emptyVOTES.executeUpdate();
        PreparedStatement emptyVOTES_REVIEW = con.prepareStatement("TRUNCATE TABLE VOTES_REVIEW");
        emptyVOTES_REVIEW.executeUpdate();        
        PreparedStatement emptyCat = con.prepareStatement("TRUNCATE TABLE BUSINESSCATEGORY");
        emptyCat.executeUpdate();
        PreparedStatement emptyAtt = con.prepareStatement("TRUNCATE TABLE BUSNATTR");
        emptyAtt.executeUpdate();
        System.out.println("Tables cleaned up...........");
        System.out.println("Starting insertion...........");
        Populate insert = new Populate();
        insert.populateYelp_Users();
        insert.populateBusiness();
        insert.populateReviews();
        insert.populateFriends();
        insert.populateUserVote();
        insert.populateReviewVote();
        insert.populateCatAndAttr();
        db.closeConnection(con);
    }
    
    public void populateYelp_Users() throws SQLException, IOException, java.text.ParseException{
        FileReader fr = new FileReader("E:/Download/YelpDataset/YelpDataset-CptS451/yelp_user.json");
        BufferedReader br = new BufferedReader(fr);
        con.setAutoCommit(true);
        PreparedStatement userSQL = null;
        System.out.println("Parsing User data..........");
        long user_count=0;
        while ((line = br.readLine()) != null){
            JsonElement jsonElement = new JsonParser().parse(line);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            
            String date = jsonObject.get("yelping_since").getAsString();
            long review_count = jsonObject.get("review_count").getAsLong();
            String user_name = jsonObject.get("name").getAsString();
            String user_id = jsonObject.get("user_id").getAsString();
            long fans = jsonObject.get("fans").getAsLong();
            double average_stars = jsonObject.get("average_stars").getAsDouble();
            String user_type = jsonObject.get("type").getAsString();
            if (userSQL == null) {
                String sql = "INSERT INTO yelp_users VALUES(?,?,?,?,?,?,?)";
                userSQL = con.prepareStatement(sql);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            java.util.Date parsed = format.parse(date.toString());
            userSQL.setDate(1,new Date(parsed.getTime()));
            userSQL.setLong(2,review_count);
            userSQL.setString(3, user_name);
            userSQL.setString(4,user_id);
            userSQL.setLong(5, fans);
            userSQL.setDouble(6, average_stars);
            userSQL.setString(7,user_type);
            userSQL.executeUpdate();
            user_count ++;
        }
        System.out.println("Completed!\nUser_count = " +user_count);
        userSQL.close();
        br.close();
    }

    public void populateBusiness() throws IOException, SQLException {
        FileReader fr = new FileReader("E:/Download/YelpDataset/YelpDataset-CptS451/yelp_business.json");
        BufferedReader br = new BufferedReader(fr);
        con.setAutoCommit(true);
        PreparedStatement businessSQL = null;
        System.out.println("Parsing Business data..........");
        long business_count = 0;
        
        while ((line = br.readLine()) != null) {
            JsonElement jsonElement = new JsonParser().parse(line);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String business_id = jsonObject.get("business_id").getAsString();
            String full_address = jsonObject.get("full_address").getAsString();
            String open = jsonObject.get("open").getAsString();
            String city = jsonObject.get("city").getAsString();
            long review_count = jsonObject.get("review_count").getAsLong();
            String business_name = jsonObject.get("name").getAsString();
            double longitude = jsonObject.get("longitude").getAsDouble();
            double latitude = (double) jsonObject.get("latitude").getAsDouble();
            String state = jsonObject.get("state").getAsString();
            double stars = (double) jsonObject.get("stars").getAsDouble();
            JsonObject attribute = jsonObject.get("attributes").getAsJsonObject();
            String attributes = attribute.toString();
            String type = jsonObject.get("type").getAsString();
            
            
//            System.out.println(business_id + "\n" + full_address + "\n" + city + "\n" + review_count + "\n" + business_name
//              + "\n" + longitude + "\n" + latitude + "\n" + open + "\n" + state + "\n" + stars + "\n"  + type + "\n"
//              + attributes+ "\n\n");

            
            if (businessSQL == null) {
                String sql = "INSERT INTO BUSINESS VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
                businessSQL = con.prepareStatement(sql);
            }
            businessSQL.setString(1,business_id);
            businessSQL.setString(2,full_address);
            businessSQL.setString(3,open);
            businessSQL.setString(4,city);
            businessSQL.setLong(5,review_count);
            businessSQL.setString(6,business_name);
            businessSQL.setDouble(7,longitude);
            businessSQL.setString(8,state);
            businessSQL.setDouble(9,stars);
            businessSQL.setDouble(10,latitude);
            businessSQL.setString(11,type);
            businessSQL.setString(12,attributes);
            businessSQL.executeUpdate();
            business_count ++;
        }
        System.out.println("Completed!\nBusiness_count = "+business_count);
        businessSQL.close();
        br.close();
    }


    public void populateReviews() throws IOException, SQLException, ParseException {
        FileReader fr = new FileReader("E:/Download/YelpDataset/YelpDataset-CptS451/yelp_review.json");
        BufferedReader br = new BufferedReader(fr);
        con.setAutoCommit(true);
        PreparedStatement reviewSQL = null;
        System.out.println("Parsing Review data..........");
        long count= 0;
        
        while ((line = br.readLine()) != null) {
            JsonElement jsonElement = new JsonParser().parse(line);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            
            String review_id = jsonObject.get("review_id").getAsString();
            String user_id = jsonObject.get("user_id").getAsString();
            String business_id = jsonObject.get("business_id").getAsString();
            String text = jsonObject.get("text").getAsString();
            String type = jsonObject.get("type").getAsString();
            double stars = (double) jsonObject.get("stars").getAsDouble();
            String datee = jsonObject.get("date").getAsString();

            if (reviewSQL == null) {
                String sql = "INSERT INTO REVIEWS VALUES(?,?,?,?,?,?,?)";
                reviewSQL = con.prepareStatement(sql);
            }
            SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD");
            java.util.Date parsed = format.parse(datee.toString());
            reviewSQL.setString(1,user_id);
            reviewSQL.setString(2,review_id);
            reviewSQL.setDouble(3, stars);
            reviewSQL.setDate(4,new Date(parsed.getTime()));
            reviewSQL.setString(5,text);
            reviewSQL.setString(6,type);
            reviewSQL.setString(7,business_id);
            reviewSQL.executeUpdate();
            count++;
        }
        System.out.println("Completed!\nReview data = "+count);
        reviewSQL.close();
        br.close();
    }    
    
    
    public void populateFriends() throws IOException, SQLException {
        FileReader fr = new FileReader("E:/Download/YelpDataset/YelpDataset-CptS451/yelp_user.json");
        BufferedReader br = new BufferedReader(fr);
        con.setAutoCommit(true);
        PreparedStatement userSQL = null;
        System.out.println("Parsing Friends data..........");

        while ((line = br.readLine()) != null) {
            JsonElement jsonElement = new JsonParser().parse(line);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String user_name = jsonObject.get("name").getAsString();
            String user_id = jsonObject.get("user_id").getAsString();
            JsonArray friendsList = jsonObject.get("friends").getAsJsonArray();
            Iterator<JsonElement> iterator = friendsList.iterator();
            while (iterator.hasNext()) {
                String friend = iterator.next().getAsString();
                //System.out.println(user_name + "\n" + user_id + "\n" + friend + "\n");
                if (userSQL == null) {
                    String sql = "INSERT INTO FRIENDS VALUES(?,?)";
                    userSQL = con.prepareStatement(sql);
                }
                userSQL.setString(1,user_id);
                userSQL.setString(2,friend);
                userSQL.executeUpdate();
            }
        }
        System.out.println("Completed!");
        userSQL.close();
        br.close();       
    }
    
    
    public void populateUserVote() throws IOException, SQLException {
        FileReader fr = new FileReader("E:/Download/YelpDataset/YelpDataset-CptS451/yelp_user.json");
        BufferedReader br = new BufferedReader(fr);
        con.setAutoCommit(true);
        PreparedStatement voteSQL = null;
        System.out.println("Parsing User_votes..........");
        long count =0;     
        
        while ((line = br.readLine()) != null) {
            JsonElement jsonElement = new JsonParser().parse(line);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String user_id = jsonObject.get("user_id").getAsString();
            JsonObject votes = jsonObject.get("votes").getAsJsonObject();
            long cool =(long) votes.get("cool").getAsLong();
            long useful = (long) votes.get("useful").getAsLong();
            long funny = (long) votes.get("funny").getAsLong();
            if(voteSQL == null ){
              String query = "INSERT INTO VOTES_USER VALUES(?,?,?,?)";
              voteSQL = con.prepareStatement(query);
            }
            voteSQL.setString(1, user_id);
            voteSQL.setLong(2, funny);
            voteSQL.setLong(3, useful);
            voteSQL.setLong(4, cool);
            voteSQL.executeQuery();
            count++;
        }
        System.out.println("Completed!\nUser_votes count = " + count);
        voteSQL.close();
        br.close();
    }
    
    

    public void populateReviewVote() throws IOException, SQLException {
        FileReader fr = new FileReader("E:/Download/YelpDataset/YelpDataset-CptS451/yelp_review.json");
        BufferedReader br = new BufferedReader(fr);
        con.setAutoCommit(true);
        PreparedStatement voteSQL = null;
        System.out.println("Parsing ReviewVotes..........");
        long count =0;     
        
        while ((line = br.readLine()) != null) {
            JsonElement jsonElement = new JsonParser().parse(line);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String user_id = jsonObject.get("user_id").getAsString();
            JsonObject votes = jsonObject.get("votes").getAsJsonObject();
            long cool =(long) votes.get("cool").getAsLong();
            long useful = (long) votes.get("useful").getAsLong();
            long funny = (long) votes.get("funny").getAsLong();
            String review_id = jsonObject.get("review_id").getAsString();
            String business_id = jsonObject.get("business_id").getAsString();
            if(voteSQL == null ){
              String query = "INSERT INTO VOTES_REVIEW VALUES(?,?,?,?,?,?)";
              voteSQL = con.prepareStatement(query);
            }
            voteSQL.setLong(1, funny);
            voteSQL.setLong(2,useful);
            voteSQL.setLong(3, cool);
            voteSQL.setString(4, user_id);
            voteSQL.setString(5, review_id);
            voteSQL.setString(6, business_id);
            voteSQL.executeQuery();
            count++;
        }
        System.out.println("Completed!\nReviewVotes count = " + count);
        voteSQL.close();
        br.close();
    }
    
    

    public void populateCatAndAttr()throws ParseException, IOException, SQLException, java.text.ParseException {
        String[] Cat = {"Active Life" ,"Arts & Entertainment", "Automotive", "Car Rental", "Cafes", "Beauty & Spas", 
                        "Convenience Stores", "Dentists", "Doctors", "Drugstores", "Department Stores", "Education", 
                        "Event Planning & Services", "Flowers & Gifts", "Food", "Health & Medical", "Home Services", "Home & Garden", 
                        "Hospitals", "Hotels & Travel", "Hardware Stores", "Grocery", "Medical Centers", "Nurseries & Gardening",
                        "Nightlife", "Restaurants", "Shopping", "Transportation"};
        
        PreparedStatement BusnCat = con.prepareStatement("INSERT INTO BUSINESSCATEGORY (BID, BUSNCATGNAME, BUSNSUBCAT) VALUES (?,?,?)");
        PreparedStatement BusAttr = con.prepareStatement("INSERT INTO BUSNATTR (BID, ATTRJSON) VALUES (?, ?)");
        FileReader fr = new FileReader("E:/Download/YelpDataset/YelpDataset-CptS451/yelp_business.json");
        BufferedReader br = new BufferedReader(fr); 
        System.out.println("Parsing Categories and Attributes ..........");
        int counter = 0;
        while ((line = br.readLine()) != null) {
            JsonElement jsonElement = new JsonParser().parse(line);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            
            String BID = jsonObject.get("business_id").getAsString();
            JsonArray alCategories = jsonObject.get("categories").getAsJsonArray();
            JsonObject sAttributes = jsonObject.get("attributes").getAsJsonObject();
 
            if(sAttributes != null){
                Set<Map.Entry<String, JsonElement>> entries = sAttributes.entrySet();
                Iterator<Map.Entry<String, JsonElement>> it = entries.iterator();
                String s = null;
                while (it.hasNext()) {
                    Map.Entry<String, JsonElement> entry = it.next();
                    if(entry.getValue() instanceof JsonObject){
                        JsonObject nest = entry.getValue().getAsJsonObject();
                        Set<Map.Entry<String, JsonElement>> t = nest.entrySet();
                        Iterator<Map.Entry<String, JsonElement>> that = t.iterator();
                        while (that.hasNext()) {
                            Map.Entry<String, JsonElement> next = that.next();
                            s = entry.getKey() + "_" +  next.getKey() + "_" + next.getValue().getAsString();
                            BusAttr.setString(1, BID);
                            BusAttr.setString(2, s);
                            BusAttr.addBatch();
                        }
                    }
                    else {
                        s = entry.getKey() + "_" + entry.getValue().getAsString();
                        BusAttr.setString(1, BID);
                        BusAttr.setString(2, s);
                        BusAttr.addBatch();
                    }
                }                  
                BusAttr.executeBatch();

            }
            
            if(alCategories!=null){
                ArrayList<String> mainCat = new ArrayList();
                ArrayList<String> subCat = new ArrayList();
                for(int i = 0; i < alCategories.size(); i++){
                    String str = alCategories.get(i).getAsString();
                    boolean isCat = false;
                    for (int j = 0; j < Cat.length; j++) {
                        if (Cat[j].equals(str)) {
                            isCat = true;
                            break;
                        }
                    }
                    if(isCat == true)
                        mainCat.add(str);
                    else
                        subCat.add(str);
                }
                if(mainCat.isEmpty()){
                    mainCat.add("null");
                }
                if (subCat.isEmpty() ){
                    subCat.add("null");
                }
                for(String mc : mainCat){
                    for(String sc : subCat){
                        BusnCat.setString(1, BID);
                        BusnCat.setString(2, mc);
                        BusnCat.setString(3, sc);
                        BusnCat.addBatch();
                        counter++;
                        if(counter>1000){
                            BusnCat.executeBatch();
                            counter = 0;
                        }
                    }
                }
            }
            BusAttr.executeBatch();
            BusnCat.executeBatch();
        }
        System.out.println("Completed!");
    }
}
