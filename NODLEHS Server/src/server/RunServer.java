package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JTextArea;
import javax.swing.UIManager;

import display.LaunchWindow;

public class RunServer {

	
	
	
    /**
     * The port that the server listens on.
     */
    private static final int PORT = 9001;

    /**
     * The set of all names of clients in the chat room.  Maintained
     * so that we can check that new clients are not registering name
     * already in use.
     */
    private static HashSet<String> names = new HashSet<String>();

    /**
     * The set of all the print writers for all the clients.  This
     * set is kept so we can easily broadcast messages.
     */
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    /**
     * The appplication main method, which just listens on a port and
     * spawns handler threads.
     */
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        
        LaunchWindow frame;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		frame = new LaunchWindow();
		frame.setVisible(true);
		JTextArea textBar = frame.getInputTextArea();
		JTextArea console = frame.getConsole();
		JTextArea board = frame.getBoard();
		board.setText("Server start\n");
		
        try {
            while (true) {
            	
            	Handler handler = new Handler(listener.accept());
            	handler.setBoard(board);
            	handler.setConsole(console);
            	handler.setTextBox(textBar);
            	handler.start();
            	
                //new Handler(listener.accept()).start();
                
                
            }
        } finally {
            listener.close();
        }
    }

    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for a dealing with a single client
     * and broadcasting its messages.
     */
    private static class Handler extends Thread {
        private String name;
        private String password;
        private String lineIn;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        JTextArea textBox;
        JTextArea console;
        JTextArea board; 
        
        
        JTextArea getTextBox(){return this.textBox;}
        JTextArea getConsole(){return this.console;}
        JTextArea getBoard(){return this.board;}
        void setTextBox(JTextArea textBoxIn){this.textBox = textBoxIn;}
        void setBoard(JTextArea boardIn){this.board = boardIn;}
        void setConsole(JTextArea consoleIn){this.console = consoleIn;}

        /**
         * Constructs a handler thread, squirreling away the socket.
         * All the interesting work is done in the run method.
         */
        public Handler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Services this thread's client by repeatedly requesting a
         * screen name until a unique one has been submitted, then
         * acknowledges the name and registers the output stream for
         * the client in a global set, then repeatedly gets inputs and
         * broadcasts them.
         */
        public void run() {
            try {
            	
            	this.board.setText(this.board.getText()+"new client joined\n");
            	
                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Request a name from this client.  Keep requesting until
                // a name is submitted that is not already used.  Note that
                // checking for the existence of a name and adding the name
                // must be done while locking the set of names.
                while (true) {
                    out.println("SUBMITNAME");
                    board.append("Asking for login information\n");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (names) {
                        if (checkUser(name)) {
                            board.append("User exists: "+name+"\n");
                            out.println("SUBMITPASS");
                            board.append("requesting password\n");
                            password = in.readLine();
                            if(password == null)
                            	return;
                            board.append("password entered: "+password+"\n");
                            if(checkPassword(name, password, board)){
                            	board.append("password accepted");
                            	out.println("ACCOUNTACCEPTED");
                            	break;
                            }
                            
                        }
                        else{
                        	board.append("User does not exist, asking if it should be created.\n");
                        	out.println("INVALIDNAME");
                        	lineIn = in.readLine();
                        	console.append(lineIn);
                        	if(lineIn.toUpperCase().equals("YES") || lineIn.toUpperCase().equals("Y"))
                        	{
                        		out.println("SUBMITNEWPASS");
                        		String pass = in.readLine();
                        		out.println("VERIFYPASS");
                        		String pass2 = in.readLine();
                        		if(pass2.equals(pass)){
                        			board.append("Passwords match");
                        			int rValue = createAccount(name, lineIn);
                        			board.append(rValue+"\n");
                            			//out.println("ACCOUNTCREATED");
                  
                        		}
                        		else{
                        			board.append("Password mismatch\n");
                        			out.println("MISMATCHPASS");
                        			//break;
                        		}
                        	}
                        	
                        }
                       
                    }
                }

                // Now that a successful name has been chosen, add the
                // socket's print writer to the set of all writers so
                // this client can receive broadcast messages.
                out.println("NAMEACCEPTED");
                writers.add(out);

                // Accept messages from this client and broadcast them.
                // Ignore other clients that cannot be broadcasted to.
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    boolean log = false;
                    for (PrintWriter writer : writers) {
                    	if(!log){
                    		console.append(name + ": " + input+"\n");
                    		log = true;
                    	}
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                // This client is going down!  Remove its name and its print
                // writer from the sets, and close its socket.
            	board.append("Client disconnected\n");
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        
        public boolean checkUser(String name)
        {
        	try{
        		Scanner scanner = new Scanner(getClass().getResourceAsStream("/Files/"+name+".ini"));
        	}
        	catch(Exception e)
        	{
        		return false;
        	}
        	
        	return true;
        }
        public boolean checkPassword(String name, String pass, JTextArea board)
        {
        	board.append("checking password\n");
        	Scanner scanner = null;
        	try{
        		scanner = new Scanner(getClass().getResourceAsStream("/Files/"+name+".ini"));
        		
        	}
        	catch(Exception e)
        	{
        		board.append("scanner failed\n");
        		return false;
        	}
        	board.append("scanner passed\n");
        	
        	Map<String, Map<String, String>> fullMap = createAssociativeArray(scanner);
        	board.append("printing pass from file\n");
        	board.append("password from file: "+fullMap.get("account").get("password")+"\n");
        	board.append("Should have printed\n");
        	if(fullMap.get("account").get("password").equals(pass))
        	{
        		return true;
        	}
        	
        	return false;
        }
        
        public int createAccount (String name, String pass)
        {
        	File file = null;
        	try{
        		file = new File(("./Files/"+name+".ini"));
        		file.getParentFile().mkdirs(); 
        		file.createNewFile();
        		Scanner scanner = new Scanner(getClass().getResourceAsStream("/Files/"+name+".ini"));
	        	FileWriter fw = new FileWriter(file.getAbsoluteFile());
	            BufferedWriter bw = new BufferedWriter(fw);
	            // write in file
	            bw.write("[account]\npassword = "+pass+"\n\n[location]\nx = 3\ny = 3\n\n[inventory]\nslot1 = log");
	            // close connection
	            bw.close();
	        }
	    	catch(Exception e)
	    	{
	    		return 2;
	    	}
        	return 0;
        }
        
        public Map<String, Map<String, String>> createAssociativeArray(Scanner scanner)
        {
        	Map<String, String> attributes = new HashMap<String, String>();
        	Map<String, Map<String, String>> fullMap = new HashMap<String, Map<String, String>>();
        	
        	String title = "";
        	while(scanner.hasNextLine())
        	{
        		String line = scanner.nextLine();
        		if(line.contains("["))
        		{
        			line.substring(1, line.length()-1);
        			title = line;
        			
        		}
        		else if(line.length()>0)
        		{
        			String[] attribute = line.split("=");
        			attributes.put(attribute[0].trim(), attribute[1].trim());
        			fullMap.put(title, attributes);
        			attributes = new HashMap<String, String>();
        		}
        	}
        	
        	
        	return fullMap;
        }
    }
}
