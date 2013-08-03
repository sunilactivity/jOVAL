# Copyright (C) 2012 jOVAL.org.  All rights reserved.
# This software is licensed under the AGPL 3.0 license available at http://www.joval.org/agpl_v3.txt
#
function Get-LockoutPolicy {
  $ErrorActionPreference = "Continue"
  $result = [jOVAL.LockoutPolicy.Probe]::getLockoutPolicy()
  foreach($token in $result) {
    Write-Output $token
  }
}
