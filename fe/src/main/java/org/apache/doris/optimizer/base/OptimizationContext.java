// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.optimizer.base;

import com.google.common.collect.Lists;
import org.apache.doris.optimizer.MultiExpression;
import org.apache.doris.optimizer.OptGroup;
import org.apache.doris.optimizer.Optimizer;

import java.util.List;

// Optimization context store information when optimizing a OptGroup
public class OptimizationContext {
    private long id;

    // only used to debug
    private OptGroup group;

    private RequiredPhysicalProperty reqdPhyProp;
    private OptCostContext bestCostCtx;

    private List<OptimizationContext> childrenOptContext;

    public OptimizationContext(OptGroup group,
                               RequiredPhysicalProperty reqdPhyProp) {
        this.group = group;
        this.reqdPhyProp = reqdPhyProp;
        this.childrenOptContext = Lists.newArrayList();
    }

    public void setBestCostCtx(OptCostContext costContext) {
        this.bestCostCtx = costContext;
    }
    public OptGroup getGroup() { return group; }
    public RequiredPhysicalProperty getReqdPhyProp() { return reqdPhyProp; }
    public OptCostContext getBestCostCtx() { return bestCostCtx; }
    public void addChildOptContext(OptimizationContext context) { this.childrenOptContext.add(context); };
    public List<OptimizationContext> getChildrenOptContext() { return childrenOptContext; };
    public MultiExpression getBestMultiExpr() { return bestCostCtx == null ? null : bestCostCtx.getMultiExpr(); }

    @Override
    public int hashCode() {
        return reqdPhyProp.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OptimizationContext)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        OptimizationContext rhs = (OptimizationContext) obj;
        return reqdPhyProp.equals(rhs.reqdPhyProp);
    }
}
