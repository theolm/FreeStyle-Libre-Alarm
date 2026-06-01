#!/bin/bash
# Script para gerar o keystore de release
# Execute este script localmente para criar o keystore

KEYSTORE_FILE="release.keystore"
KEY_ALIAS="freestylelibre"
VALIDITY_DAYS=10000

if [ -f "$KEYSTORE_FILE" ]; then
    echo "Keystore já existe: $KEYSTORE_FILE"
    exit 0
fi

echo "Gerando keystore para release..."
keytool -genkey -v \
    -keystore $KEYSTORE_FILE \
    -alias $KEY_ALIAS \
    -keyalg RSA \
    -keysize 2048 \
    -validity $VALIDITY_DAYS \
    -storepass freestylelibre \
    -keypass freestylelibre \
    -dname "CN=FreeStyle Libre Alarm, OU=Mobile, O=Dev, L=City, ST=State, C=BR"

echo "Keystore gerado: $KEYSTORE_FILE"
echo ""
echo "Para configurar no GitHub Actions, execute:"
echo "  base64 -i $KEYSTORE_FILE | pbcopy"
echo ""
echo "E cole o resultado no Secret KEYSTORE do repositório."
echo ""
echo "Secrets necessários:"
echo "  KEYSTORE: base64 do arquivo $KEYSTORE_FILE"
echo "  KEYSTORE_PASSWORD: freestylelibre"
echo "  KEY_ALIAS: $KEY_ALIAS"
echo "  KEY_PASSWORD: freestylelibre"
