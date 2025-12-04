@echo off
echo ================================================
echo   Analizador Sintactico LR(1)
echo   Esteban Ramirez - Universidad del Norte
echo ================================================
echo.

echo Compilando...
javac -encoding UTF-8 *.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Compilacion exitosa!
    echo.
    echo Ejecutando aplicacion...
    echo.
    java LR1App
) else (
    echo.
    echo Error en la compilacion.
    pause
)
