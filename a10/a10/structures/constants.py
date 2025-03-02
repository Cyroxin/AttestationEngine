# Copyright 2021 Nokia
# Licensed under the BSD 3-Clause License.
# SPDX-License-Identifier: BSD-3-Clause

"""This module contains the return codes used in the returncode structure"""

# General success code
SUCCESS = 0

# General error codes
GENERALERROR = 1000
MISSINGFIELDS = 1100
NORESULT = 1200

# Element management code
ADDITEMFAIL = 1200
ITEMDOESNOTEXIST = 1201
DELETEITEMFAIL = 1202
UPDATEITEMFAIL = 1203
EVALREADYEXISTS = 1250

# Claim codes
CLAIMSUCCESSFUL = SUCCESS
ADDCLAIMFAIL = 1300
UNKNOWNTAPROTOCOL = 2000

# Result codes
RESULTSUCCESSFUL = SUCCESS


# Verification code
VERIFYSUCCEED = SUCCESS
VERIFYFAIL = 9001
VERIFYERROR = 9002
VERIFYNORESULT = 9100

# Rule codes
RULESUCCESS = SUCCESS
UNKNOWNRULE = 4500


# Protocol Failures
PROTOCOLSUCCESS = SUCCESS
PROTOCOLEXECUTIONFAILURE = 4000
PROTOCOLNETWORKFAILURE = 4002
UNREGISTEREDPROTOCOL = 4001
