package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static void main(String[] args)throws IOException {
        //Create socket
        ServerSocket servsock = new ServerSocket(1234);
        System.out.println("Сервер запущен и готов к работе...");
        while (true){
            Socket sock = servsock.accept();
            Thread thread = new Thread(new ClientHandler(sock));
            thread.start();
        }

    }

    public static void receiveFile(Socket sock)throws IOException{
        Scanner in = new Scanner(sock.getInputStream());
        InputStream is = sock.getInputStream();
        PrintWriter pr = new PrintWriter(sock.getOutputStream(), true);


        String FileName = "/Users/daniilmarukha/IdeaProjects/se-lab04-tmp2223/serverDir/"+in.nextLine();
        int FileSize = in.nextInt();
        FileOutputStream fos = new FileOutputStream(FileName);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        byte[] filebyte = new byte[FileSize];

        int file = is.read(filebyte, 0, filebyte.length);
        bos.write(filebyte, 0, file);

        System.out.println("Incoming File: " + FileName);
        if(FileSize == file)System.out.println("File is verified");
        else System.out.println("File have some lost bytes. File Recieved " + file + " Byte");
        pr.println("File Received!");
        bos.close();
    }

    public static void sendFile(Socket socket) throws IOException {
        Scanner in = new Scanner(socket.getInputStream());
        String fileName = in.nextLine();

        File myFile = new File(fileName);

        int fileSize = (int) myFile.length();

        OutputStream os = socket.getOutputStream();
        PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));


        pw.println(fileSize);

        byte [] fileByte = new byte[fileSize];
        bis.read(fileByte,0,fileByte.length);
        os.write(fileByte,0,fileByte.length);
        System.out.println(in.nextLine());
        os.flush();
    }

    public static void deleteFile(Socket socket) throws IOException{
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
        Scanner in = new Scanner(socket.getInputStream());
        String fileName = in.nextLine();
        int id = in.nextInt();
        if (id == 0){
           printWriter.println("Error files not exists!");
        }else {
            File myFile = new File(fileName);
            if (myFile.exists()) {

                myFile.delete();
                printWriter.println("The response says that this file was deleted successfully!");
                fileName = fileName.substring(61);

                clearFile(fileName, id);

            } else if (!myFile.exists()) {
                printWriter.println("The response says that this file is not found!");
            }
        }
    }
    private static void clearFile(String fileName,int id)throws IOException{
        File files = new File("/Users/daniilmarukha/IdeaProjects/se-lab04-tmp2223/serverDir/files");
        FileReader fileReader = new FileReader(files);
        BufferedReader reader = new BufferedReader(fileReader);
        String line = "";
        String memLine = "";

        while (line != null){
            line = reader.readLine();
            if (line == null)break;
            memLine = memLine +"\n"+ line;
        }
        String replaceString = id+"_"+fileName;
        memLine = memLine.replace(replaceString,"").replace("\n","");
        FileWriter fileWriter = new FileWriter(files,false);
        fileWriter.write(memLine);
        fileWriter.flush();



        fileWriter.close();
        fileReader.close();
    }
}


class ClientHandler implements Runnable{
    Main main = new Main();
    Socket socket;
    public ClientHandler(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {

        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            int action = scanner.nextInt();
            switch (action){
                case(1) -> main.sendFile(socket);
                case (2) -> main.receiveFile(socket);
                case (3) -> main.deleteFile(socket);
            }
        } catch (IOException e) {

            System.out.println("Error");
        }

    }
}
