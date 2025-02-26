@echo on
setlocal enabledelayedexpansion

:: Configuração da API
set "URL=https://zoloft.tdec.com.br:8080/api/v1/auth"
::set "USERNAME=mcastro"
::set "PASSWORD=Hodge$404"

:: Faz a requisição e captura a resposta JSON
for /f "tokens=2 delims=:{" %%a in ('curl -s -X POST "!URL!" -H "accept: application/json" -H "Content-Type: application/json" -d "{^"username^":^"!USERNAME!^",^"password^":^"!PASSWORD!^"}"') do (
    set JWT=%%a
)

:: Removendo aspas e espaços extras
set "JWT=!JWT:"=!"
set "JWT=!JWT:}=!"
set "JWT=!JWT: =!"

:: Exibe o token na tela
echo ----------------------------------
echo JWT Token:
echo !JWT!
echo ----------------------------------

:: Copia automaticamente para a área de transferência
echo !JWT! | clip
echo Token copiado para a área de transferência!

:: Fim do script
endlocal
pause
