package client;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)throws IOException {
        Scanner scanner = new Scanner(System.in);
        Socket socket = new Socket("127.0.0.1",1234);
        PrintWriter pr = new PrintWriter(socket.getOutputStream(), true);

        System.out.println("Enter action (1 - get a file, 2 - save a file, 3 - delete a file)");
        int action = scanner.nextInt();
        pr.println(action);
        switch (action){
            case (1):{
                System.out.println("Do you want to get the file by name or by id (1 - name, 2 - id):");
                int idName = scanner.nextInt();
                switch (idName){
                    case (1):{
                        System.out.println("Enter name:");
                        String name = scanner.nextLine();
                        name = scanner.nextLine();
                        receiveFile(socket,name);
                        break;
                    }
                    case(2):{
                        System.out.println("Enter id:");
                        int id = scanner.nextInt();
                        String name = getNameById(id);

                        receiveFile(socket,name);
                    }
                }
                break;
            }
            case (2):{
                System.out.println("Enter name of the file:");
                String name = scanner.nextLine();
                name = scanner.nextLine();
                sendFile(socket,name);
                break;
            }
            case (3):{
                System.out.println("Do you want to delete the file by name or by id (1 - name, 2 - id):");
                int nameId = scanner.nextInt();
                switch (nameId){
                    case (1):{
                        System.out.println("Enter name of the file:");
                        String name = scanner.nextLine();
                        name = scanner.nextLine();
                        deleteFile(socket,name);
                        break;
                    }
                    case(2):{
                        System.out.println("Enter id:");
                        int id = scanner.nextInt();
                        String name = getNameById(id);
                        deleteFile(socket,name);
                    }
                }
            }
        }


        socket.close();
    }

    private static void sendFile(Socket socket,String fileName) throws IOException {
        Scanner scanner = new Scanner(System.in);

        File myFile = new File("/Users/daniilmarukha/IdeaProjects/se-lab04-tmp2223/clientDir/"+fileName);
        int fileSize = (int) myFile.length();

        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
        Scanner in = new Scanner(socket.getInputStream());

        pw.println(fileName);
        pw.println(fileSize);

        byte [] fileByte = new byte[fileSize];
        bis.read(fileByte,0,fileByte.length);
        os.write(fileByte,0,fileByte.length);
        System.out.println(in.nextLine());
        os.flush();


        Main main = new Main();
        int id = main.idRand();
        System.out.println("The request was sent. Response says that file is saved! ID: "+id);

        writeFile(id,fileName);
    }


    private static void receiveFile(Socket sock,String name)throws IOException{
        Scanner scanner = new Scanner(System.in);
        Scanner in = new Scanner(sock.getInputStream());
        InputStream is = sock.getInputStream();
        PrintWriter pr = new PrintWriter(sock.getOutputStream(), true);


        String fileName = "/Users/daniilmarukha/IdeaProjects/se-lab04-tmp2223/serverDir/"+name;
        pr.println(fileName);
        fileName ="/Users/daniilmarukha/IdeaProjects/se-lab04-tmp2223/clientDir/"+ fileName.substring(61);


        int fileSize = in.nextInt();
        FileOutputStream fos = new FileOutputStream(fileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        byte[] filebyte = new byte[fileSize];

        int file = is.read(filebyte, 0, filebyte.length);
        bos.write(filebyte, 0, file);


        if(fileSize == file)System.out.println("File is fully");
        else System.out.println("We have some bytes lost " + file + " Byte");
        pr.println("File Received.");
        bos.close();
    }

    private int idRand() throws IOException {
        Random random = new Random();
        File files = new File("/Users/daniilmarukha/IdeaProjects/se-lab04-tmp2223/serverDir/files");
        FileReader fileReader = new FileReader(files);
        BufferedReader reader = new BufferedReader(fileReader);
        String line = "";
        String memLine = "";
        int id = random.nextInt(20,100);
        while (line != null){
            line = reader.readLine();
            memLine = memLine +" "+ line;
        }
        if(memLine.contains(String.valueOf(id))) id = idRand();

        return id;
    }

    private static void deleteFile(Socket sock,String name)throws IOException{
        Scanner scan = new Scanner(sock.getInputStream());
        PrintWriter pr = new PrintWriter(sock.getOutputStream(), true);
        String fileName = "/Users/daniilmarukha/IdeaProjects/se-lab04-tmp2223/serverDir/"+name;
        pr.println(fileName);
        int id = getIdByName(name);
        pr.println(id);
        String answer = scan.nextLine();
        System.out.println(answer);
    }

    private static void writeFile(int id,String name) throws IOException {
        FileWriter fileWriter = new FileWriter("/Users/daniilmarukha/IdeaProjects/se-lab04-tmp2223/serverDir/files",true);
        fileWriter.write("\n"+String.valueOf(id)+"_"+name);
        fileWriter.flush();
    }


    private static String getNameById(int id) throws IOException {
        File files = new File("/Users/daniilmarukha/IdeaProjects/se-lab04-tmp2223/serverDir/files");
        FileReader fileReader = new FileReader(files);
        BufferedReader reader = new BufferedReader(fileReader);
        String line = "";
        String memLine = "";
        while (line != null){
            line = reader.readLine();
            if(line.contains(String.valueOf(id))){
                memLine = line.substring(3);

                return memLine;
            }
        }
        return "Error";
    }
    private static int getIdByName(String name) throws IOException{
        File files = new File("/Users/daniilmarukha/IdeaProjects/se-lab04-tmp2223/serverDir/files");
        FileReader fileReader = new FileReader(files);
        BufferedReader reader = new BufferedReader(fileReader);
        String line = "";
        String memLine = "";
        int id = 0;
        while (line != null){
            line = reader.readLine();
            if(line.contains(String.valueOf(name))){
                memLine = line.substring(0,2);
                id = Integer.parseInt(memLine);

                return id;
            }
        }
        return id;
    }
}
