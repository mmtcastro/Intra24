@echo off
setlocal

:: Configurações
set MAVEN_HOME=C:\path\to\maven
set TARGET_DIR=C:\Users\mcast\git\Intra24\target
set JAR_NAME=intra24-1.0-SNAPSHOT.jar
set REMOTE_USER=root
set REMOTE_HOST=suntory.tdec.com.br
set REMOTE_PATH=/usr/src/intra24
set PASSWORD=C0r0p3@201

:: Build da aplicação
echo 🔨 Compilando o projeto...
cd C:\Users\mcast\git\Intra24
call mvn clean package -Pproduction

:: Copiar o arquivo JAR para o servidor Linux via SCP
echo 📤 Enviando arquivo para o servidor...
pscp -pw %PASSWORD% "%TARGET_DIR%\%JAR_NAME%" %REMOTE_USER%@%REMOTE_HOST%:%REMOTE_PATH%

:: Conectar ao servidor via SSH e fazer o deploy
echo 🚀 Iniciando deploy no servidor...
plink -batch -pw %PASSWORD% root@suntory.tdec.com.br "bash /usr/srv/intra24/deploy.sh"


echo ✅ Deploy finalizado com sucesso!
exit

:: pscp -pw C0r0p3@201 C:\Users\mcast\git\Intra24\target\intra24-1.0-SNAPSHOT.jar root@suntory.tdec.com.br:/usr/src/intra24/
plink -batch -pw C0r0p3@201  root@suntory.tdec.com.br "echo Teste de Conexão"

plink -batch -pw C0r0p3@201 root@suntory.tdec.com.br "bash /usr/srv/intra24/deploy.sh"


