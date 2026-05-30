@echo off
echo ========================================
echo   Starting University Material Client
echo ========================================
echo.
java -cp "bin;lib/*" --module-path "lib" --add-modules javafx.controls,javafx.fxml com.university.client.MainClient
pause1