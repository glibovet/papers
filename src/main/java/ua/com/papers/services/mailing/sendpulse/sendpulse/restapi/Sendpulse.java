package ua.com.papers.services.mailing.sendpulse.sendpulse.restapi;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ua.com.papers.services.mailing.sendpulse.pherialize.Pherialize;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Sendpulse implements SendpulseInterface{

	 private String apiUrl = "https://api.sendpulse.com";
     private String userId = null;
     private String secret = null;
     private String tokenName = null;
     private int refreshToken = 0;

     public Sendpulse(String _userId, String _secret ) {
         this.userId = _userId;
         this.secret = _secret;
         try {
			this.tokenName = md5( this.userId + "::" + this.secret );
		 } catch (NoSuchAlgorithmException e) {
		 } catch (UnsupportedEncodingException e) {}
         if( this.tokenName!=null) {
             if( !this.getToken() ) {
            	 System.out.println( "Could not connect to api, check your ID and SECRET" );
             }
         }
     }
     public String md5(String param) throws NoSuchAlgorithmException, UnsupportedEncodingException {
    	 StringBuilder hexString = new StringBuilder();
    	 try {
             MessageDigest md = MessageDigest.getInstance("MD5");
             byte[] thedigest = md.digest(param.getBytes());
             for (int i = 0; i < thedigest.length; i++) {
                 hexString.append(Integer.toString((thedigest[i] & 0xff) + 0x100, 16).substring(1));
             }
         } catch (NoSuchAlgorithmException e) {
             return e.toString();
         }
         return hexString.toString();
     }
     /**
      * Get token and store it
      *
      * @return bool
      */
     private boolean getToken() {
    	 Map<String, Object> data = new HashMap<String, Object>();
    	 data.put("grant_type", "client_credentials");
 		 data.put("client_id", this.userId);
 		 data.put("client_secret", this.secret);
         Map<String, Object> requestResult = null;
         try {
        	 requestResult = this.sendRequest( "oauth/access_token", "POST", data, false );
		 } catch (IOException e) {}
         if(requestResult==null) return false;
         if(Integer.parseInt(requestResult.get("http_code").toString()) != 200 ) {
             return false;
         }
         this.refreshToken = 0;
         JSONObject jdata = (JSONObject) requestResult.get("data");
         if (jdata instanceof JSONObject){
        	 this.tokenName = jdata.get("access_token").toString();
         }
         return true;
     }
     /**
      * Make post data string 
      * @param data
      * @return
      * @throws UnsupportedEncodingException
      */
     private StringBuilder makePostDataParamsString(Map<String, Object> data) throws UnsupportedEncodingException {
    	 StringBuilder postData = new StringBuilder();
    	 if(data!=null){
	        for (Entry<String,Object> param : data.entrySet()) {
	            if (postData.length() != 0) postData.append('&');
	            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
	            postData.append('=');
	            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
	        }
    	 }
    	 return postData;
     }

     private Map<String, Object> sendRequest(String path, String method, Map<String, Object> data , boolean useToken) throws IOException {
    	 Map<String, Object> returndata = new HashMap<String, Object>();
    	 StringBuilder postData = new StringBuilder();
    	 if(data!=null && data.size()>0){
	        postData = this.makePostDataParamsString(data);
    	 }
    	 method = method.toUpperCase();
    	 if(method.equals("GET")){
    		 path = path+"?"+postData.toString();
    	 }
    	 URL obj = new URL(this.apiUrl+ "/" + path);
    	 HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
    	 if(useToken && this.tokenName!=null ) {
			con.setRequestProperty("Authorization", "Bearer " +this.tokenName);
		 }
    	 con.setRequestMethod(method);
    	 if(!method.equals("GET")){
		 	if(method.equals("PUT")) con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
			con.setDoOutput(true);
			con.setDoInput(true);
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		    wr.write(postData.toString());
			wr.flush();
			wr.close();
    	 }
    	 InputStream inputStream = null;
		 try{
		    inputStream = con.getInputStream();
		 }
		 catch(IOException exception) {
		    inputStream = con.getErrorStream();
		 }
		 int responseCode = con.getResponseCode();
    	 if(inputStream!=null){
	    	 BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			 String inputLine;
			 StringBuffer response = new StringBuffer();
		     while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			 }
		     in.close();
	    	 if( responseCode == 401 && this.refreshToken == 0 ) {
	             this.refreshToken += 1;
	             this.getToken();
	             returndata = this.sendRequest( path, method, data,false);
	         } else {
	        	 Object jo = null;
	        	 try {
	        		 jo =  new JSONObject(response.toString());
	        	     } catch (JSONException ex) {
	        	    	 try {
	        	        	jo = new JSONArray(response.toString());
	        	    	 } catch (JSONException ex1) {}
	        	    }
	             returndata.put("data",jo);
	             returndata.put("http_code", responseCode);
	         }
     	 }
    	 return returndata;
     }

     private Map<String, Object> handleResult(Map<String, Object> data ) {
    	 if( data.get("data")==null ) {
        	 data.put("data", null);
         }
         if( Integer.parseInt(data.get("http_code").toString()) != 200 ) {
        	 data.put("is_error", true);
         }
         return data;
     }

     private Map<String, Object> handleError(String customMessage ) {
    	 Map<String, Object> data = new HashMap<String, Object>();
    	 data.put("is_error", true);
         if( customMessage!=null && customMessage.length()>0 ) {
             data.put("message", customMessage);
         }
         return data;
     }

     public Map<String, Object> listAddressBooks(int limit, int offset){
    	 Map<String, Object> data = new HashMap<String, Object>();
 		 if(limit>0) data.put("limit", limit);
 		 if(offset>0) data.put("offset", offset);
 		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "addressbooks", "GET", data,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
     }

     public Map<String, Object> getBookInfo(int id ){
    	 if(id<=0) return this.handleError("Empty book id");
    	 Map<String, Object> result = null;
    	 try {
 			 result = this.sendRequest("addressbooks/"+id, "GET", null,true );
  		 } catch (IOException e) {}
         return this.handleResult(result);
     }

     public Map<String, Object> getEmailsFromBook(int id ){
    	 if(id<=0) return this.handleError("Empty book id");
    	 Map<String, Object> result = null;
    	 try {
 			 result = this.sendRequest("addressbooks/"+id+ "/emails", "GET", null,true );
  		 } catch (IOException e) {}
         return this.handleResult(result);
     }

     public Map<String, Object> removeAddressBook(int id ){
    	 if(id<=0) return this.handleError("Empty book id");
    	 Map<String, Object> result = null;
    	 try {
 			 result = this.sendRequest("addressbooks/"+id, "DELETE", null,true );
  		 } catch (IOException e) {}
         return this.handleResult(result);
     }

     public Map<String, Object> editAddressBook(int id , String newname){
    	 if(id<=0 || newname.length()==0) return this.handleError("Empty new name or book id");
    	 Map<String, Object> data = new HashMap<String, Object>();
 		 data.put("name", newname);
    	 Map<String, Object> result = null;
    	 try {
 			 result = this.sendRequest("addressbooks/"+id, "PUT", data,true );
  		 } catch (IOException e) {}
         return this.handleResult(result);
     }

     public Map<String, Object> createAddressBook(String bookName ){
    	 if(bookName.length()==0) return this.handleError("Empty book name");
    	 Map<String, Object> data = new HashMap<String, Object>();
 		 data.put("bookName", bookName);
    	 Map<String, Object> result = null;
    	 try {
 			 result = this.sendRequest("addressbooks", "POST", data,true );
  		 } catch (IOException e) {}
         return this.handleResult(result);
     }

     public Map<String, Object> addEmails(int bookId , String emails){
    	 if(bookId<=0 || emails.length()==0) return this.handleError("Empty book id or emails");
    	 Map<String, Object> data = new HashMap<String, Object>();
 		 data.put("emails", emails);
    	 Map<String, Object> result = null;
    	 try {
 			 result = this.sendRequest("addressbooks/" + bookId + "/emails", "POST", data,true );
  		 } catch (IOException e) {}
         return this.handleResult(result);
     }

     public Map<String, Object> removeEmails(int bookId , String emails){
    	 if(bookId<=0 || emails.length()==0) return this.handleError("Empty book id or emails");
    	 Map<String, Object> data = new HashMap<String, Object>();
 		 data.put("emails", emails);
    	 Map<String, Object> result = null;
    	 try {
 			 result = this.sendRequest("addressbooks/" + bookId + "/emails", "DELETE", data,true );
  		 } catch (IOException e) {}
         return this.handleResult(result);
     }

     public Map<String, Object> getEmailInfo(int bookId, String email ){
    	 if(bookId<=0 || email.length()==0) return this.handleError("Empty book id or email");
    	 Map<String, Object> result = null;
    	 try {
 			 result = this.sendRequest( "addressbooks/" + bookId + "/emails/" + email , "GET", null,true );
  		 } catch (IOException e) {}
         return this.handleResult(result);
     }

	 public Map<String, Object> campaignCost(int bookId ){
		 if(bookId<=0) return this.handleError("Empty book id");
    	 Map<String, Object> result = null;
    	 try {
 			 result = this.sendRequest( "addressbooks/" + bookId + "/cost/", "GET", null,true );
  		 } catch (IOException e) {}
         return this.handleResult(result);
	 }

	 public Map<String, Object> listCampaigns(int limit, int offset){
		 Map<String, Object> data = new HashMap<String, Object>();
 		 if(limit>0) data.put("limit", limit);
 		 if(offset>0) data.put("offset", offset);
 		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "campaigns", "GET", data,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
	 }

	 public Map<String, Object> getCampaignInfo(int id ){
		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "campaigns/"+id, "GET", null,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
	 }

	 public Map<String, Object> campaignStatByCountries(int id ){
		 if(id<=0) return this.handleError("Empty campaign id");
		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "campaigns/"+id+"/countries", "GET", null,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
	 }

     public Map<String, Object> campaignStatByReferrals(int id ){
    	 if(id<=0) return this.handleError("Empty campaign id");
		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "campaigns/"+id+"/referrals", "GET", null,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
     }


     public Map<String, Object> createCampaign(String senderName, String senderEmail, String subject, String body, int bookId, String name, String attachments){
    	 if( senderName.length()==0 || senderEmail.length()==0 || subject.length()==0 || body.length()==0 || bookId<=0 )
			  return this.handleError( "Not all data.");
    	 String encodedBody = DatatypeConverter.printBase64Binary(body.getBytes());
		 Map<String, Object> data = new HashMap<String, Object>();
		 if(attachments.length()>0) data.put("attachments", attachments);
		 data.put("sender_name", senderName);
		 data.put("sender_email", senderEmail);
		 data.put("subject", subject);
		 if(encodedBody.length()>0) data.put("body", encodedBody.toString());
		 data.put("list_id", bookId);
		 if(name.length()>0) data.put("name", name);
		 Map<String, Object> result = null;
		 try {
			result = this.sendRequest( "campaigns", "POST", data,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);          
     }

	 public Map<String, Object> cancelCampaign(int id ){
		 if(id<=0) return this.handleError("Empty campaign id");
		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "campaigns/"+id, "DELETE", null,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
	 }
	 /**
      * Get list of allowed senders
      */
	 public Map<String, Object> listSenders(){
		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "senders", "GET", null,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
	 }


	 public Map<String, Object> addSender(String senderName, String senderEmail ){
		 if(senderName.length()==0 || senderEmail.length()==0) return this.handleError("Empty sender name or email");
		 Map<String, Object> data = new HashMap<String, Object>();
		 data.put("name", senderName);
		 data.put("email", senderEmail);
 		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "senders", "POST", data,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
	 }

	 public Map<String, Object> removeSender(String email ){
		 if(email.length()==0) return this.handleError("Empty email");
		 Map<String, Object> data = new HashMap<String, Object>();
		 data.put("email", email);
		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "senders", "DELETE", data,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
	 }

	 public Map<String, Object> activateSender(String email, String code ){
		 if(email.length()==0 || code.length()==0) return this.handleError("Empty email or activation code");
		 Map<String, Object> data = new HashMap<String, Object>();
		 data.put("code", code);
		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "senders/" + email + "/code", "POST", data,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
	 }

	 public Map<String, Object> getSenderActivationMail(String email ){
		 if(email.length()==0) return this.handleError("Empty email");
		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "senders/" + email + "/code", "GET", null,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
	 }

	 public Map<String, Object> getEmailGlobalInfo(String email ){
		 if(email.length()==0) return this.handleError("Empty email");
		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "emails/" + email, "GET", null,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
	 }

	 public Map<String, Object> removeEmailFromAllBooks(String email ){
		 if(email.length()==0) return this.handleError("Empty email");
		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "emails/" + email, "DELETE", null,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
	 }

	 public Map<String, Object> emailStatByCampaigns(String email ){
		 if(email.length()==0) return this.handleError("Empty email");
		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "emails/" + email + "/campaigns", "GET", null,true );
 		 } catch (IOException e) {}
         return this.handleResult(result);
	 }
	 /**
      * Show emails from blacklist
      * @return Map<String, Object>
      */
	 public Map<String, Object> getBlackList(){
		 Map<String, Object> result = null;
 		 try {
			result = this.sendRequest( "blacklist", "GET", null,true );
 		 } catch (IOException e) {}
 		 return this.handleResult(result);
	 }

    public Map<String, Object> addToBlackList(String emails){
    	if(emails.length()==0) return this.handleError("Empty emails");
    	Map<String, Object> data = new HashMap<String, Object>();
    	String encodedemails = DatatypeConverter.printBase64Binary(emails.getBytes());
		data.put("emails", encodedemails);
    	Map<String, Object> result = null;
		try {
			result = this.sendRequest( "blacklist", "POST", data,true );
		} catch (IOException e) {}
        return this.handleResult(result);
    }

    public Map<String, Object> removeFromBlackList(String emails ){
    	if(emails.length()==0) return this.handleError("Empty emails");
    	Map<String, Object> data = new HashMap<String, Object>();
    	String encodedemails = DatatypeConverter.printBase64Binary(emails.getBytes());
		data.put("emails", encodedemails);
    	Map<String, Object> result = null;
		try {
			result = this.sendRequest( "blacklist", "DELETE", data,true );
		} catch (IOException e) {}
        return this.handleResult(result);
    }

    public Map<String, Object> getBalance(String currency){
    	String url = "balance";
    	if(currency.length()>0){
    		currency = currency.toUpperCase();
    		url = url+"/"+currency;
    	}
    	Map<String, Object> result = null;
		try {
			result = this.sendRequest( url, "GET", null,true );
		} catch (IOException e) {}
        return this.handleResult(result);
    }

    public Map<String, Object> smtpSendMail(Map<String, Object> emaildata ){
    	if(emaildata.size()==0) return this.handleError("Empty email data");
    	String html = emaildata.get("html").toString();
            html = DatatypeConverter.printBase64Binary(html.getBytes());
        emaildata.put("html", html);
    	Map<String, Object> data = new HashMap<String, Object>();
    	String serialized = Pherialize.serialize(emaildata);
    	data.put("email", serialized);
    	Map<String, Object> result = null;
		try {
			result = this.sendRequest( "smtp/emails", "POST", data,true );
		} catch (IOException e) {}
        return this.handleResult(result);
    }

    public Map<String, Object> smtpListEmails(int limit, int offset, String fromDate, String toDate, String sender, String recipient){
    	Map<String, Object> data = new HashMap<String, Object>();
    	data.put("limit", limit);
    	data.put("offset", offset);
    	if(fromDate.length()>0) data.put("fromDate", fromDate);
    	if(toDate.length()>0) data.put("toDate", toDate);
    	if(sender.length()>0) data.put("sender", sender);
    	if(recipient.length()>0) data.put("recipient", recipient);
    	Map<String, Object> result = null;
		try {
			result = this.sendRequest( "smtp/emails", "GET", data,true );
		} catch (IOException e) {}
        return this.handleResult(result);
    }

    public Map<String, Object> smtpGetEmailInfoById(String id ){
    	if(id.length()==0) return this.handleError("Empty id");
    	Map<String, Object> result = null;
		try {
			result = this.sendRequest( "smtp/emails/"+id, "GET", null,true );
		} catch (IOException e) {}
        return this.handleResult(result);
    }

    public Map<String, Object> smtpUnsubscribeEmails(String emails ){
    	if(emails.length()==0) return this.handleError("Empty emails");
    	Map<String, Object> data = new HashMap<String, Object>();
    	data.put("emails", emails);
    	Map<String, Object> result = null;
		try {
			result = this.sendRequest( "/smtp/unsubscribe", "POST", data,true );
		} catch (IOException e) {}
        return this.handleResult(result);
    }

    public Map<String, Object> smtpRemoveFromUnsubscribe(String emails ){
    	if(emails.length()==0) return this.handleError("Empty emails");
    	Map<String, Object> data = new HashMap<String, Object>();
    	data.put("emails", emails);
    	Map<String, Object> result = null;
		try {
			result = this.sendRequest( "/smtp/unsubscribe", "DELETE", data,true );
		} catch (IOException e) {}
        return this.handleResult(result);
    }
    /**
     * Get list of allowed IPs using SMTP
     * @return Map<String, Object>
     */
    public Map<String, Object> smtpListIP(){
    	Map<String, Object> result = null;
		try {
			result = this.sendRequest( "smtp/ips", "GET", null,true );
		} catch (IOException e) {}
        return this.handleResult(result);
    }
    /**
    * Get list of allowed domains using SMTP
    * @return Map<String, Object>
    */
    public Map<String, Object> smtpListAllowedDomains(){
    	Map<String, Object> result = null;
		try {
			result = this.sendRequest( "smtp/domains", "GET", null,true );
		} catch (IOException e) {}
        return this.handleResult(result);
    }

    public Map<String, Object> smtpAddDomain(String email ){
    	if(email.length()==0) return this.handleError("Empty email");
    	Map<String, Object> data = new HashMap<String, Object>();
    	data.put("email", email);
    	Map<String, Object> result = null;
		try {
			result = this.sendRequest( "smtp/domains", "POST", data,true );
		} catch (IOException e) {}
        return this.handleResult(result);
    }

    public Map<String, Object> smtpVerifyDomain(String email ){
    	if(email.length()==0) return this.handleError("Empty email");
    	Map<String, Object> result = null;
		try {
			result = this.sendRequest( "smtp/domains/"+email, "GET", null,true );
		} catch (IOException e) {}
        return this.handleResult(result);
    }
}
