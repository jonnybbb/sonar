#
# Sonar, entreprise quality control tool.
# Copyright (C) 2008-2011 SonarSource
# mailto:contact AT sonarsource DOT com
#
# Sonar is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 3 of the License, or (at your option) any later version.
#
# Sonar is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with Sonar; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
#

#
# Sonar 2.13
#
class CreateTableResourceIndex < ActiveRecord::Migration

  def self.up
    create_table 'resource_index', :id => false do |t|
      t.column 'kee', :string, :null => false, :limit => 100
      t.column 'position', :integer, :null => false
      t.column 'name_size', :integer, :null => false
      t.column 'resource_id', :integer, :null => false
      t.column 'project_id', :integer, :null => false
    end
    add_index 'resource_index', 'kee', :name => 'resource_index_key'
    add_index 'resource_index', 'resource_id', :name => 'resource_index_rid'
  end

end
