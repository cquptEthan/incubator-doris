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

#ifndef DORIS_BE_SRC_OLAP_ROWSET_ALPHA_ROWSET_H
#define DORIS_BE_SRC_OLAP_ROWSET_ALPHA_ROWSET_H

#include "olap/rowset/rowset.h"
#include "olap/rowset/segment_group.h"
#include "olap/rowset/alpha_rowset_reader.h"
#include "olap/rowset/alpha_rowset_writer.h"
#include "olap/rowset/rowset_meta.h"
#include "olap/data_dir.h"
#include "olap/tuple.h"

#include <vector>
#include <memory>

namespace doris {

class AlphaRowset;
using AlphaRowsetSharedPtr = std::shared_ptr<AlphaRowset>;

class AlphaRowset : public Rowset {
public:
    AlphaRowset(const TabletSchema* schema,
                std::string rowset_path,
                DataDir* data_dir,
                RowsetMetaSharedPtr rowset_meta);

    virtual ~AlphaRowset() {}

    static bool is_valid_rowset_path(std::string path);

    OLAPStatus init() override;

    // this api is for lazy loading data
    // always means that there are some io
    OLAPStatus load(bool use_cache = true) override;

    std::shared_ptr<RowsetReader> create_reader() override;

    OLAPStatus remove() override;

    OLAPStatus link_files_to(const std::string& dir, RowsetId new_rowset_id) override;

    OLAPStatus copy_files_to(const std::string& dir) override;

    OLAPStatus convert_from_old_files(const std::string& snapshot_path,
                                 std::vector<std::string>* success_files);

    OLAPStatus convert_to_old_files(const std::string& snapshot_path, 
                                 std::vector<std::string>* success_files);

    OLAPStatus remove_old_files(std::vector<std::string>* files_to_remove) override;

    OLAPStatus split_range(
            const RowCursor& start_key,
            const RowCursor& end_key,
            uint64_t request_block_row_count,
            vector<OlapTuple>* ranges);
    
    bool check_path(const std::string& path) override;

    // when convert from old be, should set row num, index size, data size
    // info by using segment's info
    OLAPStatus reset_sizeinfo();

protected:
    // add custom logic when rowset is published
    void make_visible_extra(Version version, VersionHash version_hash) override;

private:
    OLAPStatus _init_segment_groups();

    std::shared_ptr<SegmentGroup> _segment_group_with_largest_size();

private:
    friend class AlphaRowsetWriter;
    friend class AlphaRowsetReader;

    std::vector<std::shared_ptr<SegmentGroup>> _segment_groups;
};

} // namespace doris

#endif // DORIS_BE_SRC_OLAP_ROWSET_ALPHA_ROWSET_H
