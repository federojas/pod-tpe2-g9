# pod-tpe2-g9

Trabajo Práctico 2 para Programación de Objetos Distribuidos. ITBA 2Q 2022.

## Instalación

Clonar el proyecto y ejecutar los siguiente comandos:

```bash
cd pod-tpe2-g9
mvn clean install
```
En los directorios server/target y client/target están los archivos de extensión .tar.gz que deben ser descomprimidos. 

Comandos para descomprimir, y luego otorgar permisos de ejecución a los scripts obtenidos al descomprimir los .tar.gz.


```bash
cd server/target
tar -xvf tpe2-g9-server-1.0-SNAPSHOT-bin.tar.gz
cd tpe2-g9-server-1.0-SNAPSHOT
chmod u+x run-server

cd ..
cd ..
cd ..
cd client/target
tar -xvf tpe2-g9-client-1.0-SNAPSHOT-bin.tar.gz
cd tpe2-g9-client-1.0-SNAPSHOT
chmod u+x query*

```


## Uso

Para realizar todos los pasos anteriores puede utilizar el script build.sh

Para encender el server ejecutar el script "run-server"

```bash
./run-server
```


Luego puede ejecutar los clientes de las distintas queries de esta forma:

Observacion: Para las queries 3 y 4 existen parámetros adicionales

## queryX
```bash
./queryX 
    -Daddresses='xx.xx.xx.xx:XXXX;yy.yy.yy.yy:YYYY' 
    -DinPath=XX 
    -DoutPath=YY [params]

```
donde:
- queryX es el script que corre la query X.
- -Daddresses refiere a las direcciones IP de los nodos con sus puertos (una o más, separadas por punto y coma)
- -DinPath indica el path donde están los archivos de entrada sensors.csv y  readings.csv.
- -DoutPath indica el path donde estarán ambos archivos de salida queryX.csv y timeX.txt.
- [params]: los parámetros extras que corresponden para algunas queries.

## Ejemplos de invocacion para cada query

```bash
./query1 
    -Daddresses='10.6.0.1:5701' 
    -DinPath=.
    -DoutPath=.
```

```bash
./query2 
    -Daddresses='10.6.0.1:5701' 
    -DinPath=.
    -DoutPath=.
```

```bash
./query3 
    -Daddresses='10.6.0.1:5701' 
    -DinPath=.
    -DoutPath=. -Dmin=10000
```
donde:
- min es un valor entero mayor a cero que indica la minima cantidad de mediciones asentada
```bash
./query4 
    -Daddresses='10.6.0.1:5701' 
    -DinPath=.
    -DoutPath=. -Dn=3 -Dyear=2021
```
donde:
- n es la cantidad máxima de sensores a listar
- year es el año a consultar
Ambos parámetros son enteros mayores a cero

```bash
./query5 
    -Daddresses='10.6.0.1:5701' 
    -DinPath=.
    -DoutPath=.
```
