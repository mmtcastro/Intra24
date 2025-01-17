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

# Sample script for creating a Domino Keyring using the KYRTOOL.

# CONSULT WITH YOUR ORGANIZATION'S SECURITY EXPERT BEFORE USING.

# CONSULT DOCUMENTATION BEFORE USING:
# https://doc.cwpcollaboration.com/appdevpack/tlstools.html

# CUSTOMIZE: Set the parameters below.

# The Domino server data directory
export NOTESDATA=${NOTESDATA-/local/notesdata}
# The Domino server binaries directory
export NOTESBIN=${NOTESBIN-/opt/hcl/domino/bin/tools}
# The root certificate file name
ROOTCA_CRT=ca.crt
# The server private key file
SERVER_KEY=server.key
# The server certificate file
SERVER_CRT=server.crt
# The output KYRFILE name
KYRFILE=$NOTESDATA/appdevpack.kyr
# The output STHFILE name
STHFILE=$NOTESDATA/appdevpack.sth
# The password to the KYRFILE
KYRPASS=1234567890

function kyrtool() {
	(set -x ; cd $NOTESDATA; $NOTESBIN/startup kyrtool $* )
}

if [[ -f "$KYRFILE" ]]
then
	echo "Keyring file already exists: $KYRFILE"
	exit 1
fi

if [[ -f "$STHFILE" ]]
then
	echo "Keyring sth file already exists: $STHFILE"
	exit 1
fi

if [[ ! -f "$SERVER_KEY" || ! -f "$SERVER_CRT" || ! -f "$ROOTCA_CRT" ]]
then
	echo "This tool uses files created by the make_certs.sh script."
	echo "Consider running this script first."
	exit 1
fi

# Create the empty keyring file with the password
kyrtool create -k "$KYRFILE" -p "$KYRPASS"

# Certificates and private key must be in leaf first order in the file. The
# final certificate in the chain will be marked as a trusted root.
readonly tmpfile=$(mktemp)
cat "$SERVER_KEY" "$SERVER_CRT" "$ROOTCA_CRT" > $tmpfile
kyrtool import all -i $tmpfile -k "$KYRFILE"
rm -f $tmpfile


# Show contents from the keyring
kyrtool show keys -k "$KYRFILE"
kyrtool show certs -k "$KYRFILE"
kyrtool show roots -k "$KYRFILE" -v
