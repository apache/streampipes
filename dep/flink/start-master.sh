#!/usr/bin/env bash

FLINK_HOME="/opt/flink"
FLINK_BIN_DIR="${FLINK_HOME}/bin"
FLINK_CONF_DIR="${FLINK_HOME}/conf"
FLINK_MASTER_HOSTNAME="flink-master"
FLINK_MASTER_WEBUI_PORT=8081
EXECUTIONMODE="cluster"

echo "$(hostname -i) ${FLINK_MASTER_HOSTNAME}" >> /etc/hosts

. "${FLINK_BIN_DIR}"/config.sh

if [[ ! ${FLINK_JM_HEAP} =~ $IS_NUMBER ]] || [[ "${FLINK_JM_HEAP}" -lt "0" ]]; then
    echo "[ERROR] Configured JobManager memory size is not a valid value. Please set '${KEY_JOBM_MEM_SIZE}' in ${FLINK_CONF_FILE}."
    exit 1
fi

if [ "${FLINK_JM_HEAP}" -gt "0" ]; then
    export JVM_ARGS="$JVM_ARGS -Xms"$FLINK_JM_HEAP"m -Xmx"$FLINK_JM_HEAP"m"
fi

# Startup parameters
args=("--configDir" "${FLINK_CONF_DIR}" "--executionMode" "${EXECUTIONMODE}")
if [ ! -z $HOST ]; then
    args+=("--host")
    args+=("${HOST}")
fi

if [ ! -z $WEBUIPORT ]; then
    args+=("--webui-port")
    args+=("${WEBUIPORT}")
fi

"${FLINK_BIN_DIR}"/start-common.sh jobmanager "${args[@]}"
