#!/bin/bash -eu
##########################################################################
#                                                                        #
# Licensed Materials - Property of HCL                                   #
#                                                                        #
# (c)Copyright HCL Technologies Ltd. 2018, 2020. All Rights Reserved.    #
#                                                                        #
# Note to U.S.Government Users Restricted Rights :                       #
# Use, duplication or disclosure restricted by GSA ADP Schedule          #
# Contract with IBM Corp.                                                #
#                                                                        #
##########################################################################

# Sample script to create a certificate authority and 3 signed keys.

# CONSULT WITH YOUR ORGANIZATION'S SECURITY EXPERT BEFORE USING.

# CONSULT DOCUMENTATION BEFORE USING:
# https://doc.cwpcollaboration.com/appdevpack/tlstools.html

# Common function for creating a new key and signing it by the certificate
# authority. The process of creating a private key and signing the key is
# customarily done by separate processes. This script combines the two
# operations, but is not considered a best practice.
function create_leaf() {
	LOG="CREATE_LEAF $1: "
	NAME="$1"
	SUBJ="$2"
	SANS="$3"

	if [[ -f $NAME.key ]]
	then
		echo "$LOG File already exists: $NAME.key"
		return
	fi
	if [[ -f $NAME.crt ]]
	then
		echo "$LOG File already exists: $NAME.crt"
		return
	fi

	echo ""
	echo "$LOG Generating $NAME.key"
	(set -x ; openssl genrsa -passout "$LEAF_PASSWORD" -des3 -out $NAME.key 4096)

	echo ""
	echo "$LOG Generate Certificate Sign Request - CSR"
	(set -x ; openssl req -passin "$LEAF_PASSWORD" -new -key $NAME.key -out $NAME.csr -subj "$SUBJ" -sha256)

	echo ""
	echo "$LOG CA Signs CSR, Creates certificate"
	if [[ -z "$SANS" ]]
	then
		(set -x ; openssl x509 -passin "$ROOT_PASSWORD" -req -days "$LEAF_VALIDITY" -in $NAME.csr -CA ca.crt -CAkey ca.key -out $NAME.crt -CAcreateserial -CAserial ca.seq)
	else
		(set -x ; openssl x509 -passin "$ROOT_PASSWORD" -req -days "$LEAF_VALIDITY" -in $NAME.csr -CA ca.crt -CAkey ca.key -out $NAME.crt -CAcreateserial -CAserial ca.seq -extfile <(printf "subjectAltName=$SANS"))
	fi

	echo ""
	echo "$LOG Remove passphrase from Key"
	(set -x ; openssl rsa -passin "$LEAF_PASSWORD" -in $NAME.key -out $NAME.key)

	echo ""
	echo "$LOG Remove CSR"
	(set -x ; rm $NAME.csr)
}

function create_root() {
	LOG="CREATE_ROOT: "
	if [[ -f ca.key || -f ca.crt ]]
	then
		echo "$LOG CA already exists."
	else
		# Generate CA private key
		(set -x ; openssl genrsa -passout "$ROOT_PASSWORD" -des3 -out ca.key 4096)
		# Self-Sign CA key
		(set -x ; openssl req -passin "$ROOT_PASSWORD" -new -x509 -days "$ROOT_VALIDITY" -key ca.key -out ca.crt -subj "$ROOT_SUBJECT" -sha256)
	fi
}

# CUSTOMIZE: Set the parameters below.

# The subject name of the root certificate.
ROOT_SUBJECT="/O=TDec/CN=TDecAppDevCA"
# The number of days the root certificate will be valid.
ROOT_VALIDITY=36500
# The password to create and access the root certificate.
ROOT_PASSWORD=pass:faria201
# Number of days leaf certificate(s) will be valid.
LEAF_VALIDITY=36500
# The password to create and access the leaf certificate(s).
LEAF_PASSWORD=pass:faria201

create_root

# CUSTOMIZE: Change the list of certificates to create and their attributes.
# Parameters to create_leaf: "file-name" "subject-name" "optional-subject-alternate-names"
create_leaf server "/O=TDec/CN=zoloft.tdec.com.br" "DNS:zoloft.tdec.com.br"
create_leaf app1 "/O=TDec/CN=app1" ""
create_leaf app2 "/O=TDec/CN=app2" ""

# Show details for certificates
for f in *.crt
do
	echo ""
	(set -x ; openssl x509 -in $f -text -noout -certopt no_pubkey,no_sigdump)
done
