/**
 * Licensed to JumpMind Inc under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership.  JumpMind Inc licenses this file
 * to you under the GNU General Public License, version 3.0 (GPLv3)
 * (the "License"); you may not use this file except in compliance
 * with the License.
 *
 * You should have received a copy of the GNU General Public License,
 * version 3.0 (GPLv3) along with this library; if not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
#ifndef SYM_PURGE_SERVICE_H
#define SYM_PURGE_SERVICE_H

#include <stdio.h>
#include "util/Date.h"
#include "util/List.h"
#include "service/ParameterService.h"
#include "db/SymDialect.h"

typedef enum {
    DATA, DATA_EVENT, OUTGOING_BATCH, STRANDED_DATA
} SymMinMaxDeleteSql;

typedef struct SymPurgeService {
    SymParameterService *parameterService;
    SymDialect *symmetricDialect;
    long (*purgeIncoming)(struct SymPurgeService *this);
    long (*purgeOutgoing)(struct SymPurgeService *this);
    long (*purgeIncomingBeforeDate)(struct SymPurgeService *this, SymDate *retentionCutoff);
    long (*purgeOutgoingBeforeDate)(struct SymPurgeService *this, SymDate *retentionCutoff);

    void (*destroy)(struct SymPurgeService *);
} SymPurgeService;

SymPurgeService * SymPurgeService_new(SymPurgeService *this, SymParameterService *parameterService,
        SymDialect *symmetricDialect);

#endif
