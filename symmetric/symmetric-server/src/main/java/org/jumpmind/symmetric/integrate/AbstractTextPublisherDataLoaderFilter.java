/*
 * Licensed to JumpMind Inc under one or more contributor 
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding 
 * copyright ownership.  JumpMind Inc licenses this file
 * to you under the GNU Lesser General Public License (the
 * "License"); you may not use this file except in compliance
 * with the License. 
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see           
 * <http://www.gnu.org/licenses/>.
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.jumpmind.symmetric.integrate;

import org.jumpmind.db.model.Table;
import org.jumpmind.log.Log;
import org.jumpmind.log.LogFactory;
import org.jumpmind.symmetric.ext.INodeGroupExtensionPoint;
import org.jumpmind.symmetric.io.data.CsvData;
import org.jumpmind.symmetric.io.data.DataContext;
import org.jumpmind.symmetric.io.data.DataEventType;
import org.jumpmind.symmetric.io.data.IDataReader;
import org.jumpmind.symmetric.io.data.IDataWriter;
import org.jumpmind.symmetric.io.data.writer.DatabaseWriterFilterAdapter;
import org.springframework.beans.factory.BeanNameAware;

/**
 * An abstract convenience class meant to be implemented by classes that need to
 * publish text messages
 */
abstract public class AbstractTextPublisherDataLoaderFilter extends DatabaseWriterFilterAdapter
        implements IPublisherFilter, INodeGroupExtensionPoint, BeanNameAware {

    private final Log log = LogFactory.getLog(getClass());

    private final String MSG_CACHE = "msg_CACHE" + hashCode();

    protected IPublisher publisher;

    private boolean loadDataInTargetDatabase = true;

    protected String tableName;

    private String[] nodeGroupIdsToApplyTo;

    private int messagesSinceLastLogOutput = 0;

    private long minTimeInMsBetweenLogOutput = 30000;

    private long lastTimeInMsOutputLogged = System.currentTimeMillis();

    private String beanName;

    protected abstract String addTextHeader(
            DataContext<? extends IDataReader, ? extends IDataWriter> context);

    protected abstract String addTextElement(
            DataContext<? extends IDataReader, ? extends IDataWriter> context, Table table, CsvData data);

    protected abstract String addTextFooter(
            DataContext<? extends IDataReader, ? extends IDataWriter> context);

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public <R extends IDataReader, W extends IDataWriter> boolean beforeWrite(
            DataContext<R, W> context, Table table, CsvData data) {
        if (tableName != null && tableName.equals(table.getName())) {
            DataEventType eventType = data.getDataEventType();
            if (eventType.isDml()) {
                String msg = addTextElement(context, table, data);
                if (msg != null) {
                    getFromCache(context).append(msg);
                }
            }
        }
        return loadDataInTargetDatabase;
    }

    protected StringBuilder getFromCache(
            DataContext<? extends IDataReader, ? extends IDataWriter> context) {
        StringBuilder msgCache = (StringBuilder) context.get(MSG_CACHE);
        if (msgCache == null) {
            msgCache = new StringBuilder(addTextHeader(context));
            context.put(MSG_CACHE, msgCache);
        }
        return msgCache;
    }

    protected boolean doesTextExistToPublish(
            DataContext<? extends IDataReader, ? extends IDataWriter> context) {
        StringBuilder msgCache = (StringBuilder) context.get(MSG_CACHE);
        return msgCache != null && msgCache.length() > 0;
    }

    private void finalizeAndPublish(
            DataContext<? extends IDataReader, ? extends IDataWriter> context) {
        StringBuilder msg = getFromCache(context);
        if (msg.length() > 0) {
            msg.append(addTextFooter(context));
            log.debug("Publishing text message %s", msg);
            context.remove(MSG_CACHE);
            publisher.publish(context, msg.toString());
        }
    }

    public <R extends IDataReader, W extends IDataWriter> void batchComplete(
            DataContext<R, W> context) {
        if (doesTextExistToPublish(context)) {
            finalizeAndPublish(context);
            logCount();
        }
    }

    protected void logCount() {
        messagesSinceLastLogOutput++;
        long timeInMsSinceLastLogOutput = System.currentTimeMillis() - lastTimeInMsOutputLogged;
        if (timeInMsSinceLastLogOutput > minTimeInMsBetweenLogOutput) {
            log.info("%s published %d messages in the last %d ms.", beanName, messagesSinceLastLogOutput,
                    timeInMsSinceLastLogOutput);
            lastTimeInMsOutputLogged = System.currentTimeMillis();
            messagesSinceLastLogOutput = 0;
        }
    }

    public void setLoadDataInTargetDatabase(boolean loadDataInTargetDatabase) {
        this.loadDataInTargetDatabase = loadDataInTargetDatabase;
    }

    public void setPublisher(IPublisher publisher) {
        this.publisher = publisher;
    }

    public String[] getNodeGroupIdsToApplyTo() {
        return nodeGroupIdsToApplyTo;
    }

    public void setNodeGroupIdToApplyTo(String nodeGroupdId) {
        this.nodeGroupIdsToApplyTo = new String[] { nodeGroupdId };
    }

    public void setMessagesSinceLastLogOutput(int messagesSinceLastLogOutput) {
        this.messagesSinceLastLogOutput = messagesSinceLastLogOutput;
    }

    public void setMinTimeInMsBetweenLogOutput(long timeInMsBetweenLogOutput) {
        this.minTimeInMsBetweenLogOutput = timeInMsBetweenLogOutput;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}