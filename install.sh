#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -e

# Usage:
#   curl ... | ENV_VAR=... sh -
#       or
#   ENV_VAR=... ./install.sh
#
# Example:
#   Directory to installing StreamPipes binary (needs sudo)
#   curl ... | INSTALL_SP_BIN_DIR="/usr/local/bin" sh -
#
# Environment variables:
#
#   - INSTALL_SP_BIN_DIR
#     Directory to install StreamPipes binary
#     default: /usr/local/bin

GIT_CLI_URL=https://github.com/streampipes/streampipes-cli/tarball/master
#TODO: change SP_BACKEND_VERSION based on Maven Version in gitlab-ci.yml
#SP_BACKEND_VERSION=0.60.0
DEBUG=false

if [ "$1" = "--debug" ]; then
  DEBUG=true
fi

# --- helper functions for logs ---
info(){
  echo "[INFO]\t" "$@"
}

debug() {
  if $DEBUG; then
    echo "[DEBUG]\t" "$@"
  fi
}

warning() {
  echo "[WARN]\t" "$@"
}

fatal(){
  echo "[ERROR]\t" "$@"
  exit 1
}

install_notice() {
	echo
	echo
	echo "  StreamPipes CE will now be installed on your system"
	echo
	echo
}

uninstall_notice() {
	echo
	echo
	echo "  To uninstall StreamPipes CE run the following command:"
  echo
  echo "  Linux:"
	echo "  \$ sudo sp-uninstall "
  echo
  echo "  Mac:"
	echo "  \$ sp-uninstall "
  echo
}

# --- helper functions ---
semverParseDocker() {
	major_docker="${1%%.*}"
	minor_docker="${1#$major_docker.}"
	minor_docker="${minor_docker%%.*}"
	patch_docker="${1#$major_docker.$minor_docker.}"
	patch_docker="${patch_docker%%[-.]*}"
}

semverParseDockerCompose() {
	major_docker_compose="${1%%.*}"
	minor_docker_compose="${1#$major_docker_compose.}"
	minor_docker_compose="${minor_docker_compose%%.*}"
}

command_exists() {
	command -v "$@" > /dev/null 2>&1
}

check_and_add_to_path() {
  SP_HOME=$1
  debug "Add SP_HOME to PATH"
  case ":${PATH:=$SP_HOME}:" in
    *:$SP_HOME:*)
      debug "SP_HOME found in PATH"
      ;;
    *)
      s=$(echo $SHELL)
      currShell=${s##*/}
      case $currShell in
        "bash")
          debug "Detected shell: $currShell"
          if [ $2 = "user-only" ] ; then
            debug "Check if SP_HOME exists in $HOME/.bashrc"

            if grep -q SP_HOME "$HOME/.bashrc"; then
              # found
              info "[SKIPPED] SP_HOME already set"
            else
              # not found
              info "Add SP_HOME to $HOME/.bashrc"
              echo "export SP_HOME=$SP_HOME" >> $HOME/.bashrc
              echo 'export PATH=$PATH:$SP_HOME' >>  $HOME/.bashrc
            fi

          elif [ $2 = "system-wide" ]; then
            debug "Check if SP_HOME exists in /etc/profile.d/streampipes-env.sh"

            if [ -f "/etc/profile.d/streampipes-env.sh" ]; then
              # found
              info "[SKIPPED] SP_HOME already set"
            else
              # not found
              info "Add SP_HOME to /etc/profile.d/streampipes-env.sh"
              tee /etc/profile.d/streampipes-env.sh >/dev/null << EOF
#!/bin/sh
SP_HOME="$SP_HOME"
if [ -d "\$SP_HOME" ] ; then
    PATH="\$SP_HOME:\$PATH"
fi
EOF
              chmod 755 /etc/profile.d/streampipes-env.sh
            fi

          else
            warning "SP_HOME not set for $currShell. Set manually"
          fi
          ;;
        "zsh")
          debug "Detected shell: $currShell"
          if [ $2 == "user-only" ]; then
            debug "Check if SP_HOME exists in $HOME/.zshrc"

            if grep -q SP_HOME "$HOME/.zshrc"; then
              # found
              info "[SKIPPED] SP_HOME already set"
            else
              # not found
              info "Add SP_HOME to $HOME/.zshrc"
              echo "export SP_HOME=$SP_HOME" >> $HOME/.zshrc
              echo 'export PATH=$PATH:$SP_HOME' >> $HOME/.zshrc
            fi

          elif [ $2 == "system-wide" ]; then
            debug "Check if SP_HOME exists in /etc/zsh/zshenv"

            if grep -q SP_HOME "/etc/zsh/zshenv"; then
              # found
              info "[SKIPPED] SP_HOME already set"
            else
              # not found
              info "Add SP_HOME to /etc/zsh/zshenv"
              echo "export SP_HOME=$SP_HOME" >> /etc/zsh/zshenv
              echo 'export PATH=$PATH:$SP_HOME' >> /etc/zsh/zshenv
            fi

          else
            warning "SP_HOME not set for $currShell. Set manually"
          fi
          ;;
        *)
          warning "Could not detect shell environment. Manually export SP_HOME=$SP_HOME and add to PATH"
          ;;
      esac
      ;;
  esac
}

# --- functions ---
setup_env() {

  if [ $OS_TYPE = "Linux" ]; then
    SP_HOME="/opt/streampipes"
    if [ ! -d $SP_HOME ]; then
      info "Create and set StreamPipes Home (SP_HOME): $SP_HOME"
      $SUDO mkdir -p $SP_HOME

      check_and_add_to_path $SP_HOME system-wide

    else
      info "[SKIPPED] StreamPipes Home already exists"
      check_and_add_to_path $SP_HOME system-wide

    fi
  elif [ $OS_TYPE = "Mac" ]; then
    SP_HOME="$HOME/streampipes"
    if [ ! -d $SP_HOME ]; then
      info "Create and set StreamPipes Home (SP_HOME): $SP_HOME"
      mkdir -p $SP_HOME

      check_and_add_to_path $SP_HOME user-only

    else
      info "[SKIPPED] StreamPipes Home already exists"
      check_and_add_to_path $SP_HOME user-only

    fi
  fi

    # --- use binary install directory if defined or create default ---
  if [ -n "${INSTALL_SP_BIN_DIR}" ]; then
      BIN_DIR="${INSTALL_SP_BIN_DIR}"
  else
      BIN_DIR="/usr/local/bin"
  fi

   UNINSTALL_SP_SH=sp-uninstall

  # --- use sudo if we are not already root ---
  SUDO=sudo
  if [ `id -u` = 0 ]; then
      SUDO=
  fi

}

# --- fatal if no curl ---
verify_curl() {
  info "Verifying curl"
  if [ -z `which curl || true` ]; then
    fatal "Cannot find curl for downloading files"
  fi
}

# --- fatal if architecture not supported ---
verify_arch() {
  info "Verifying system architecture"
  ARCH=`uname -m`
  case $ARCH in
    amd64)
        ARCH=amd64
        SUFFIX=
        debug "Supported architecture detected: $ARCH"
        ;;
    x86_64)
        ARCH=amd64
        SUFFIX=
        debug "Supported architecture detected: $ARCH"
        ;;
    *)
        fatal "Unsupported architecture: $ARCH"
  esac
}

# --- fatal if OS not supported ---
verify_os() {
  info "Verifying OS"
  OS_TYPE="$(uname -s)"
  case $OS_TYPE in
    Linux*)
        OS_TYPE=Linux
        debug "Supported OS detected: $OS_TYPE"
        ;;
    Darwin*)
        OS_TYPE=Mac
        debug "Supported OS detected: $OS_TYPE"
        ;;
    *)
        fatal "Unsupported O: $OS_TYPE"
  esac
}

# --- fatal if Docker/Docker Compose not installed or version mismatch ---
verify_docker() {
  info "Verifying Docker and Docker Compose"
  if command_exists docker && command_exists docker-compose; then
    docker_version=`docker -v | cut -d ' ' -f3 | cut -d ',' -f1`
    docker_compose_version=`docker-compose -v | cut -d ' ' -f3 | cut -d ',' -f1`

    MAJOR_W_DOCKER=1
		MINOR_W_DOCKER=10

    MAJOR_W_DOCKER_COMPOSE=1
    MINOR_W_DOCKER_COMPOSE=8

    semverParseDocker "$docker_version"
    semverParseDockerCompose "$docker_compose_version"

    shouldWarnDocker=0
    if [ "$major_docker" -lt "$MAJOR_W_DOCKER" ]; then
			shouldWarnDocker=1
		fi

    if [ "$major_docker" -le "$MAJOR_W_DOCKER" ]  && [ "$minor_docker" -lt "$MINOR_W_DOCKER" ]; then
			shouldWarnDocker=1
		fi

    shouldWarnDockerCompose=0
    if [ "$major_docker_compose" -lt "$MAJOR_W_DOCKER_COMPOSE" ]; then
      shouldWarnDockerCompose=1
    fi

    if [ "$major_docker_compose" -le "$MAJOR_W_DOCKER_COMPOSE" ]  && [ "$minor_docker_compose" -lt "$MINOR_W_DOCKER_COMPOSE" ]; then
      shouldWarnDockerCompose=1
    fi

    if [ $shouldWarnDocker -eq 1 ]; then
      fatal "Docker version $docker_version detected which is not compatible. Supported Docker version from $MAJOR_W_DOCKER.$MINOR_W_DOCKER.0+"
    fi

    if [ $shouldWarnDockerCompose -eq 1 ]; then
      fatal "Docker Compose version $docker_compose_version detected which is not compatible. Supported Docker Compose version from $MAJOR_W_DOCKER_COMPOSE.$MINOR_W_DOCKER_COMPOSE.0+"
    fi

    debug "Installed Docker version: $docker_version"
    debug "Installed Docker Compose version: $docker_compose_version"
  else
    fatal "Cannot find Docker and/or Docker Compose. Please make sure Docker and Docker Compose are installed and configured properly"
  fi
}

download_and_configure() {
  CLI_DIR=$SP_HOME/streampipes-cli
  if [ ! -d $CLI_DIR ]; then
    info "Create directory for StreamPipes CLI in SP_HOME"
    mkdir $CLI_DIR
  fi

  if [ ! "$(ls -A $CLI_DIR)" ]; then
    # SP_HOME empty
    info "Downloading StreamPipes CLI to SP_HOME"
    curl -sSfL ${GIT_CLI_URL} | tar -xzf - -C $CLI_DIR --strip-components=1 || fatal "Error while downloading StreamPipes CLI project"
    info "Copy StreamPipes CLI binary to ${BIN_DIR}/sp"
    cp $CLI_DIR/sp $BIN_DIR
  else
    info "[SKIPPED] StreamPipes CLI already exists"
  fi
}

# --- create uninstall script ---
create_uninstall() {
  info "Creating StreamPipes uninstall script in ${BIN_DIR}/${UNINSTALL_SP_SH}"
      tee ${BIN_DIR}/${UNINSTALL_SP_SH} >/dev/null << EOF
#!/bin/sh

# --- helper functions for logs ---
info(){
  echo "[INFO]\t" "\$@"
}

fatal(){
  echo "[ERROR]\t" "\$@"
  exit 1
}

s=$(echo \$SHELL)
currShell=\${s##*/}

case \$currShell in
  "zsh")
    if [ -f \$HOME/.zshrc ]; then
      info "Removing SP_HOME from \$HOME/.zshrc"
      sed -i.bak '/SP_HOME/d' \$HOME/.zshrc
      rm \$HOME/.zshrc.bak
    elif [ -f /etc/zsh/zshenv ]; then
      info "Removing SP_HOME from /etc/zsh/zshenv"
      sed -i.bak '/SP_HOME/d' /etc/zsh/zshenv
      rm /etc/zsh/zshenv.bak
    fi
    ;;
  "bash")
    if [ -f \$HOME/.bashrc ]; then
      info "Removing SP_HOME from \$HOME/.bashrc"
      sed -i.bak '/SP_HOME/d' \$HOME/.bashrc
      rm \$HOME/.bashrc
    elif [ -f /etc/profile.d/streampipes-env.sh ]; then
      info "Deleting /etc/profile.d/streampipes-env.sh"
      rm /etc/profile.d/streampipes-env.sh
    fi
    ;;
  *)
    fatal "Could not unset SP_HOME from \$currShell"
esac

info "Deleting StreamPipes Home directory ${SP_HOME}"
rm -rf ${SP_HOME}
info "Deleting StreamPipes CLI ${BIN_DIR}/sp"
rm -f ${BIN_DIR}/sp
info "Deleting StreamPipes uninstall script ${BIN_DIR}/${UNINSTALL_SP_SH}"
rm -rf ${BIN_DIR}/${UNINSTALL_SP_SH}
EOF
  #$SUDO chmod 755 ${SP_HOME}/${UNINSTALL_SP_SH}
  #$SUDO chown root:root ${SP_HOME}/${UNINSTALL_SP_SH}
  chmod 755 ${BIN_DIR}/${UNINSTALL_SP_SH}
  chown $USER:$USER ${BIN_DIR}/${UNINSTALL_SP_SH}
}

# --- start StreamPipes CLI script ---
start_cli() {
  info "Starting StreamPipes CLI"
  if command_exists sp; then
    sp start
  else
    fatal "Could not find StreamPipes CLI binary"
  fi
}

# --- run install process ---
do_install () {
  install_notice
  verify_curl
  verify_arch
  verify_os
  verify_docker
  setup_env
  download_and_configure
  create_uninstall
  uninstall_notice
  start_cli
}

do_install
