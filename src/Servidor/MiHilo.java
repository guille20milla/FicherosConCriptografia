/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import Criptografia.Clave;
import Criptografia.RSA;
import Criptografia.Dividir;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Guillermo Veintemilla Clase MiHilo que se ejecuta por cada cliente
 * que se conecta
 */
public class MiHilo extends Thread {

    private int opcion;
    ObjectOutputStream output;
    ObjectInputStream input;
    Socket conexion;
    private static final String repositorioServidor = "Repositorio\\Servidor\\";

    private Clave clavePrivada, clavePublicaCliente;

    /**
     * Constructor del hilo
     *
     * @param c
     */
    public MiHilo(Socket c) {
        this.conexion = c;
        cargarClaves();
    }

    private void cargarClaves() {
        RSA rsa = new RSA();
        clavePrivada = rsa.clavePrivada();
        Clave clavePublicaServidor = rsa.clavePublica();
        try {
            PrintStream os = new PrintStream(conexion.getOutputStream());
            os.println(clavePublicaServidor.x);
            os.println(clavePublicaServidor.n);
            os.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            BigInteger x = new BigInteger(in.readLine());
            BigInteger n = new BigInteger(in.readLine());
            clavePublicaCliente = new Clave(x, n);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo run del hilo
     */
    @Override
    public void run() {
        try {
            opcion = 0;
            output = new ObjectOutputStream(conexion.getOutputStream());
            input = new ObjectInputStream(conexion.getInputStream());
            while (true) {
                opcion = (int) input.readObject();
                switch (opcion) {
                    case 1:
                        File repositorio = new File(this.repositorioServidor);
                        File[] archivos;
                        archivos = repositorio.listFiles();
                        output.writeObject(archivos);
                        break;
                    case 2:
                        BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                        String archivo = in.readLine();
                        descargarArchivo(archivo);
                        break;
                    case 3:
                        BufferedReader in1 = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                        String archivo1 = in1.readLine();
                        subirArchivo(archivo1);
                        break;
                }
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error de conexión con el servidor", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Selecciona que quieres descargar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void descargarArchivo(String ruta) {
        try {
            File archivo = new File(ruta);
            byte[] archivoEncriptado = Files.readAllBytes(archivo.toPath());

            int tamaño = 256;
            List<byte[]> desencriptado = Dividir.dividirArray((archivoEncriptado), tamaño);
            List<byte[]> encriptado = new ArrayList<>();

            int longitud = 0;
            for (byte[] b : desencriptado) {
                byte[] encriptacion = clavePrivada.encriptar(b);
                encriptado.add(encriptacion);
                longitud += encriptacion.length;
            }
            DataOutputStream dos = new DataOutputStream(conexion.getOutputStream());
            dos.writeInt(longitud);
            for (byte[] b : encriptado) {
                dos.write(b, 0, b.length);
            }
            dos.flush();
        } catch (Exception e) {
            System.err.println("El archivo no existe.");
        }
    }

    /**
     * Metodo para subir archivos al servidor.
     *
     * @param archivoOrigen archivo que se recibe del cliente.
     * @param archivoDestino archivo donde va a ir dicho archivo.
     */
    public void subirArchivo(String ruta) {
        try {
            int bytesLeidos;
            DataInputStream clienteData = new DataInputStream(conexion.getInputStream());
            File archivo = new File(ruta);
            OutputStream output = new FileOutputStream(repositorioServidor + archivo.getName());

            int dimension = clienteData.readInt();
            byte[] buffer = new byte[256];
            while (dimension > 0 && (bytesLeidos = clienteData.read(buffer, 0, (int) Math.min(buffer.length, dimension))) != -1) {
                output.write(clavePublicaCliente.desencriptar(buffer));
                dimension -= bytesLeidos;
            }
            output.close();
        } catch (IOException ex) {
            System.err.println("Error con el cliente");
        }

    }
}
