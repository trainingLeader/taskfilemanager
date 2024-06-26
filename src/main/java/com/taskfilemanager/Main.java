package com.taskfilemanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    private static final String FILE_NAME = "tasks.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Path path, rutaPaquete;
        File archivo;

        try {
            // Obtener la ruta del directorio que contiene el archivo JAR o el directorio de clases
            path = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();

            // Mover hacia el directorio del proyecto
            Path projectDir = path.getParent();

            // Construir la ruta relativa del paquete 'src/main/java/com/filemanager/data'
            rutaPaquete = projectDir.resolve(Paths.get("src", "main", "java", "com", "taskfilemanager", "data"));
            File carpeta = rutaPaquete.toFile();
            archivo = new File(carpeta, FILE_NAME);

            // Crear la carpeta si no existe
            if (!carpeta.exists()) {
                if (carpeta.mkdirs()) {
                    System.out.println("Carpeta creada: " + carpeta.getAbsolutePath());
                } else {
                    System.out.println("Error al crear la carpeta.");
                    return;
                }
            }

            // Crear el archivo si no existe
            if (!archivo.exists()) {
                if (archivo.createNewFile()) {
                    System.out.println("Archivo de tareas creado: " + archivo.getName());
                } else {
                    System.out.println("Error al crear el archivo de tareas.");
                    return;
                }
            }

            // Menú de opciones
            int opcion;
            do {
                System.out.println("\nGestor de Tareas");
                System.out.println("1. Agregar Tarea");
                System.out.println("2. Listar Tareas");
                System.out.println("3. Buscar Tarea");
                System.out.println("4. Eliminar Tarea");
                System.out.println("5. Salir");
                System.out.print("Selecciona una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar el buffer

                switch (opcion) {
                    case 1:
                        System.out.print("Escribe la tarea a agregar: ");
                        String nuevaTarea = scanner.nextLine();
                        agregarTarea(archivo, nuevaTarea);
                        break;
                    case 2:
                        listarTareas(archivo);
                        break;
                    case 3:
                        System.out.print("Escribe la tarea a buscar: ");
                        String tareaBuscar = scanner.nextLine();
                        buscarTarea(archivo, tareaBuscar);
                        break;
                    case 4:
                        System.out.print("Escribe la tarea a eliminar: ");
                        String tareaEliminar = scanner.nextLine();
                        eliminarTarea(archivo, tareaEliminar);
                        break;
                    case 5:
                        System.out.println("Saliendo...");
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            } while (opcion != 5);

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    public static void agregarTarea(File archivo, String tarea) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo, true), StandardCharsets.UTF_8))) {
            writer.write(tarea);
            writer.newLine();
            System.out.println("Tarea agregada: " + tarea);
        } catch (IOException e) {
            System.out.println("Ocurrió un error al agregar la tarea.");
            e.printStackTrace();
        }
    }

    public static void listarTareas(File archivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            String tarea;
            System.out.println("\nLista de Tareas:");
            while ((tarea = reader.readLine()) != null) {
                System.out.println(tarea);
            }
        } catch (IOException e) {
            System.out.println("Ocurrió un error al listar las tareas.");
            e.printStackTrace();
        }
    }

    public static void buscarTarea(File archivo, String tareaBuscada) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            String tarea;
            boolean encontrada = false;
            while ((tarea = reader.readLine()) != null) {
                if (tarea.contains(tareaBuscada)) {
                    System.out.println("Tarea encontrada: " + tarea);
                    encontrada = true;
                    break;
                }
            }
            if (!encontrada) {
                System.out.println("Tarea no encontrada.");
            }
        } catch (IOException e) {
            System.out.println("Ocurrió un error al buscar la tarea.");
            e.printStackTrace();
        }
    }

    public static void eliminarTarea(File archivo, String tareaEliminar) {
        File tempFile = new File(archivo.getAbsolutePath() + ".tmp");
        boolean eliminada = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
            String tarea;
            while ((tarea = reader.readLine()) != null) {
                if (tarea.equals(tareaEliminar)) {
                    eliminada = true;
                    continue;
                }
                writer.write(tarea);
                writer.newLine();
            }
            if (!eliminada) {
                System.out.println("Tarea no encontrada para eliminar.");
            } else {
                System.out.println("Tarea eliminada: " + tareaEliminar);
            }
        } catch (IOException e) {
            System.out.println("Ocurrió un error al eliminar la tarea.");
            e.printStackTrace();
        }

        // Reemplazar el archivo original con el archivo temporal
        if (!archivo.delete()) {
            System.out.println("Error al eliminar el archivo original.");
            return;
        }
        if (!tempFile.renameTo(archivo)) {
            System.out.println("Error al renombrar el archivo temporal.");
        }
    }
}