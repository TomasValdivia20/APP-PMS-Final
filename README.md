# APP-PMS-Final
Bienvenido al README de la APP para Pasteleria Mil Sabores

Proyecto: APP Pasteleria Mil Sabores

Integrantes: Tomás Martínez y Felipe Cañete

Funcionalidades: Comprende el proyecto de una aplicación, donde se crean usuarios y se usan credenciales para comprar productos de la tienda. Por otro lado, se pueden administrar por medio de un CRUD las categorias y productos de la tienda para los empleados y administradores autorizados. 

 Endpoints utilizados:
# AuthController (/api/auth/): Gestion de autentificacion y registro de los usuarios. (PUT, POST)
# CategoriaController (/api/categorias/): Gestion de operaciones CRUD para las categorias de los productos. (GET, POST, PUT, DELETE)
# ProductoController (/api/productos/): Gestion de operaciones CRUD para los productos de la pasteleria. (GET, POST, PUT, DELETE)
# OrdenController (/api/ordenes/): Gestión de creación y lectura de ordenes de compra. (GET, POST)
# UsuarioController (/api/usuarios/): Permite la lectura de usuarios para el administrador. (GET)
# ReporteController (/api/reportes/): Provee datos estadísticos sobre ventas del negocio. (GET)
# FileController (/api/files): Maneja la subida y servicio de archivos estáticos (Pensado para Imágenes por limitaciones técnicas). (GET, POST)
# API Externa (USDA FoodData Central): Se consume esta api externa para obtener los valores nutricionales que el usuario quiera conocer de alguna comida (Se tiene que escribir en Ingles). (GET)

Pasos para ejecutar: Por motivos de recursos, no se está utilizando un backend que se encuentre disponible 24/7, por lo que se requieren realizar ciertas configuraciones para que opere como se espera.
# Iniciar laboratorio de pruebas en AWS para levantar la instancia EC2 que contiene el Backend y la Base de datos.
# Se obtiene la ipv4 publica de la instancia levantada.
# Se reemplaza la ip que se tiene en application.properties en el Backend con la de la instancia.
# En Maven aplicar clean y luego package. Al terminar, tomar la ruta del backend.jar. 
# Reemplazar backend.jar del EC2. Recordar reemplazar IP que sale al final abajo con la IP Pública de la EC2 (ej: 98.93.32.115):
# scp -i "tu_pem.pem" "ruta_donde_se_tiene_backend\backend\target\backend-0.0.1-SNAPSHOT.jar" ec2-user@insertar_ip_aca:~/backend.jar
# Iniciar sesion en EC2: ssh -i tu_pem.pem ec2-user@InsertarIPAca
# Ejecutar dentro de Backend: nohup java -jar backend.jar > log.txt 2>&1 &
# Verificar si corre: tail -f log.txt
# Ir a Frontend, editar RetrofitClient.kt para que use la IP de la instancia EC2.
# Compilar e iniciar!!!!

EN CASO DE QUE SE ESTE USANDO EL PUERTO 8081, aplicar estos comandos dentro de la instancia ec2 por medio del cmd:
# sudo lsof -i :8081
# sudo kill -9 <PID>
# java -jar backend.jar

EN CASO DE QUERER RESETEAR LA BBDD CON DATAINITIALIZER
# (Entrar a instancia EC2 por ssh) 
# pkill -f java
# mysql -u frodo -p
# DROP DATABASE pasteleria_db;
# CREATE DATABASE pasteleria_db;
# EXIT;
# java -jar backend.jar

Dentro de app/release se encuentra el apk firmado.<img width="187" height="23" alt="image" src="https://github.com/user-attachments/assets/780a6555-b1fe-445c-9dda-574fc0ac2825" /> <img width="155" height="21" alt="image" src="https://github.com/user-attachments/assets/1615933c-fdcb-42fe-8bf6-1c375c44b1bc" />


