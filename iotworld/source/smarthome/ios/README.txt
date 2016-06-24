#
#  Copyright 2014-2016 CyberVision, Inc.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

**********************************
BUILD APPLICATION
**********************************

1. Download and untar the Kaa C SDK archive.
2. Open [Kaa C SDK Home]/listfiles/platform/ios/CMakeLists.txt
3. Add the following line similar to other lines in include list:

${KAA_SRC_FOLDER}/platform-impl/posix/posix_file_utils.c

4. Build C SDK targeting iOS platform.
5. Copy headers from C SDK to [Application home]/include/kaa
6. Open application project in Xcode.
7. Drag and drop [Kaa C SDK Home]/build/Kaa-c.xcodeproj file to application project in Xcode.
8. Open Build Phases tab of SmartHouse project and add libkaac_o.a in "Link Binary With Libraries" section.
