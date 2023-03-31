"""
Entrypoint for all common scripting infrastructure.

Invoke like 'python resources <actions>'
Where actions can be any list of actions to take.

"""

import argparse
import sys
import traceback
from typing import Sequence

from mcresources import ResourceManager, utils
from mcresources.type_definitions import Json

import recipes
import data
import book


class ModificationLoggingResourceManager(ResourceManager):

    def write(self, path_parts: Sequence[str], data_in: Json):
        m = self.modified_files
        super(ModificationLoggingResourceManager, self).write(path_parts, data_in)
        if m != self.modified_files:
            print('Modified: ' + utils.resource_location(self.domain, path_parts).join(), file=sys.stderr)
            traceback.print_stack()
            print('', file=sys.stderr)


def main():
    parser = argparse.ArgumentParser(description='Generate resources for Water Flasks')
    rm = ResourceManager('waterflasks', resource_dir='./src/main/resources')
    parser.add_argument('--clean', action='store_true', dest='clean', help='Clean all auto generated resources')
    args = parser.parse_args()

    if args.clean:
        # Stupid windows file locking errors.
        for tries in range(1, 1 + 3):
            try:
                utils.clean_generated_resources('/'.join(rm.resource_dir))
                print('Clean Success')
                return
            except:
                print('Failed, retrying (%d / 3)' % tries)
        print('Clean Aborted')
        return

    generate_all(rm)
    print('New = %d, Modified = %d, Unchanged = %d, Errors = %d' % (rm.new_files, rm.modified_files, rm.unchanged_files, rm.error_files))


def generate_all(rm: ResourceManager):
    recipes.generate(rm)
    data.generate(rm)
    book.generate(rm)

    rm.flush()


if __name__ == '__main__':
    main()