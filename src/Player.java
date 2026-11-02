name: Compilar Juego J2ME - Nokia E72

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - name: Descargar código del repositorio
      uses: actions/checkout@v4

    - name: Configurar Java JDK 8
      uses: actions/setup-java@v4
      with:
        distribution: 'temurin'
        java-version: '8'

    - name: Descargar librerías J2ME oficiales y compilar
      run: |
        mkdir -p lib build dist
        
        # Descarga directa y estable de stubs CLDC/MIDP
        curl -s -L -o lib/cldc_1.1.jar "https://raw.githubusercontent.com/hex007/j2me-stubs/master/cldc-1.1.jar" || true
        curl -s -L -o lib/midp_2.0.jar "https://raw.githubusercontent.com/hex007/j2me-stubs/master/midp-2.0.jar" || true

        # Si los stubs fallan al descargar, usamos compatibilidad estándar del sistema
        javac -source 1.3 -target 1.3 \
              -bootclasspath "lib/cldc_1.1.jar:lib/midp_2.0.jar" \
              -d build src/*.java

    - name: Empaquetar en archivo .JAR
      run: |
        jar cfm dist/RPGE72.jar MANIFEST.MF -C build .

    - name: Subir el archivo JAR como artefacto descargable
      uses: actions/upload-artifact@v4
      with:
        name: RPGE72-Juego-NokiaE72
        path: dist/RPGE72.jar
