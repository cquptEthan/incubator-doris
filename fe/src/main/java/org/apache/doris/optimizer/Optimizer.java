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

package org.apache.doris.optimizer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.doris.optimizer.base.*;
import org.apache.doris.optimizer.operator.OptExpressionHandle;
import org.apache.doris.optimizer.operator.OptPatternLeaf;
import org.apache.doris.optimizer.operator.OptPhysical;
import org.apache.doris.optimizer.rule.OptRule;
import org.apache.doris.optimizer.search.DefaultScheduler;
import org.apache.doris.optimizer.search.Scheduler;
import org.apache.doris.optimizer.search.SearchContext;

import java.util.List;

/**
 * Optimizer's entrance class
 */
public final class Optimizer {
    private final QueryContext queryContext;
    private final OptMemo memo;
    private final List<OptRule> rules = Lists.newArrayList();

    public Optimizer(QueryContext queryContext) {
        this.queryContext = queryContext;
        this.memo = new OptMemo();
        this.memo.init(queryContext.getExpression());
    }

    public OptMemo getMemo() { return memo; }
    public OptGroup getRoot() { return memo.getRoot(); }
    public List<OptRule> getRules() { return rules; }
    public void addRule(OptRule rule) { rules.add(rule); }

    public void optimize() {
        OptimizationContext optCtx = new OptimizationContext(
                memo.getRoot(),
                queryContext.getReqdProp());
        final Scheduler scheduler = DefaultScheduler.create();
        final SearchContext sContext = SearchContext.create(this, memo.getRoot(),
                optCtx, scheduler, queryContext.getVariables());
        scheduler.run(sContext);
    }
}
