from enum import Enum, auto

from mcresources import ResourceManager
from mcresources.type_definitions import Json

from mcresources import utils

class Size(Enum):
    tiny = auto()
    very_small = auto()
    small = auto()
    normal = auto()
    large = auto()
    very_large = auto()
    huge = auto()


class Weight(Enum):
    very_light = auto()
    light = auto()
    medium = auto()
    heavy = auto()
    very_heavy = auto()


def generate(rm: ResourceManager):

    rm.entity_tag('drops_bladders_50', 'tfc:cow', 'tfc:musk_ox', 'tfc:yak')
    rm.entity_tag('drops_bladders_20', 'tfc:horse', 'tfc:mule', 'tfc:donkey', 'tfc:grizzly_bear', 'tfc:polar_bear', 'tfc:black_bear')
    rm.entity_tag('drops_bladders_10', 'tfc:sheep', 'tfc:alpaca', 'tfc:goat')

    rm.item_tag('waterflasks:flasks', 'waterflasks:iron_flask', 'waterflasks:leather_flask')
    item_size(rm, 'waterflasks', '#waterflasks:flasks', Size.very_small, Weight.very_heavy)

    ### MISC DATA ###
    chances = ('10', '20', '50')
    global_loot_modifiers(rm, *['waterflasks:bladders_%s' % i for i in chances])
    for chance in chances:
        global_loot_modifier(rm, 'bladders_%s' % chance, 'waterflasks:add_item', {'item': utils.item_stack('waterflasks:bladder'), 'chance': float(chance) / 100}, match_entity_tag('waterflasks:drops_bladders_%s' % chance))

    #  todo: add wildebeest, zebu, camel, llama, gazelle, deer...

def global_loot_modifier(rm: ResourceManager, name: str, mod_type: str, data_in: Json, *conditions: utils.Json):
    rm.write((*rm.resource_dir, 'data', rm.domain, 'loot_modifiers', name), {
        'type': mod_type,
        'conditions': [c for c in conditions],
        **data_in
    })


# note for the mcresources dev: these work exactly the same as tags so if you implement this, do it like that
def global_loot_modifiers(rm: ResourceManager, *modifiers: str):
    rm.write((*rm.resource_dir, 'data', 'forge', 'loot_modifiers', 'global_loot_modifiers'), {
        'replace': False,
        'entries': [m for m in modifiers]
    })


def match_entity_tag(tag: str):
    return {
        'condition': 'minecraft:entity_properties',
        'predicate': {
            'type': '#' + tag
        },
        'entity': 'this'
    }


def item_size(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, size: Size, weight: Weight):
    rm.data(('tfc', 'item_sizes', name_parts), {
        'ingredient': utils.ingredient(ingredient),
        'size': size.name,
        'weight': weight.name
    })
