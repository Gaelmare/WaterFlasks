#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from enum import Enum
from typing import Union, Tuple

from mcresources import ResourceManager, RecipeContext, utils
from mcresources.type_definitions import Json, ResourceIdentifier

from typing import List, Sequence, Optional


class Rules(Enum):
    hit_any = 'hit_any'
    hit_not_last = 'hit_not_last'
    hit_last = 'hit_last'
    hit_second_last = 'hit_second_last'
    hit_third_last = 'hit_third_last'
    draw_any = 'draw_any'
    draw_last = 'draw_last'
    draw_not_last = 'draw_not_last'
    draw_second_last = 'draw_second_last'
    draw_third_last = 'draw_third_last'
    punch_any = 'punch_any'
    punch_last = 'punch_last'
    punch_not_last = 'punch_not_last'
    punch_second_last = 'punch_second_last'
    punch_third_last = 'punch_third_last'
    bend_any = 'bend_any'
    bend_last = 'bend_last'
    bend_not_last = 'bend_not_last'
    bend_second_last = 'bend_second_last'
    bend_third_last = 'bend_third_last'
    upset_any = 'upset_any'
    upset_last = 'upset_last'
    upset_not_last = 'upset_not_last'
    upset_second_last = 'upset_second_last'
    upset_third_last = 'upset_third_last'
    shrink_any = 'shrink_any'
    shrink_last = 'shrink_last'
    shrink_not_last = 'shrink_not_last'
    shrink_second_last = 'shrink_second_last'
    shrink_third_last = 'shrink_third_last'


def generate(rm: ResourceManager):

    leather_knapping(rm, 'leather_side', ['   X ',
                                          ' XXXX',
                                          'XXXXX',
                                          ' XXX ',
                                          '     '], 'waterflasks:leather_side')

    anvil_recipe(rm, 'unfinished_iron_flask', '#forge:sheets/wrought_iron', 'waterflasks:unfinished_iron_flask', 3, Rules.punch_last, Rules.bend_second_last, Rules.bend_third_last)

    heat_recipe(rm, 'wrought_iron_unfinished_iron_flask', 'waterflasks:unfinished_iron_flask', 1535, None, '200 tfc:metal/cast_iron')

    damage_shaped(rm, 'crafting/iron_flask', [' SK', 'CBC', 'LIL'], {'I': 'waterflasks:unfinished_iron_flask',
                                                                     'K': '#tfc:knives',
                                                                     'C': 'tfc:burlap_cloth',
                                                                     'S': '#forge:string',
                                                                     'L': 'waterflasks:leather_side',
                                                                     'B': 'waterflasks:bladder'}, 'waterflasks:iron_flask')

    damage_shaped(rm, 'crafting/leather_flask', [' L ', 'SBS', ' LK'], {'K': '#tfc:knives',
                                                                        'S': '#forge:string',
                                                                        'L': 'waterflasks:leather_side',
                                                                        'B': 'waterflasks:bladder'}, 'waterflasks:leather_flask')

    damage_shaped(rm, 'crafting/leather_flask_rotated', [' S ', 'LBL', ' SK'], {'K': '#tfc:knives',
                                                                                'S': '#forge:string',
                                                                                'L': 'waterflasks:leather_side',
                                                                                'B': 'waterflasks:bladder'}, 'waterflasks:leather_flask')

    flask_repair(rm, 'crafting/repair_broken_iron', ['FB', 'CK'], {'K': '#tfc:knives', 'F': 'waterflasks:broken_iron_flask', 'C': 'tfc:burlap_cloth', 'B': 'waterflasks:bladder'}, 'waterflasks:iron_flask')
    flask_repair(rm, 'crafting/repair_broken_leather', ['FB'], {'F': 'waterflasks:broken_leather_flask', 'B': 'waterflasks:bladder'}, 'waterflasks:leather_flask')
    flask_repair(rm, 'crafting/repair_leather', ['FB'], {'F': 'waterflasks:leather_flask', 'B': 'waterflasks:bladder'}, 'waterflasks:leather_flask')
    flask_repair(rm, 'crafting/repair_iron', ['FB', 'CK'], {'K': '#tfc:knives', 'F': 'waterflasks:iron_flask', 'C': 'tfc:burlap_cloth', 'B': 'waterflasks:bladder'}, 'waterflasks:iron_flask')


def flask_repair(rm: ResourceManager, name_parts: utils.ResourceIdentifier, pattern: Sequence[str], ingredients: Json, result: Json) -> RecipeContext:
    return delegate_recipe(rm, name_parts, 'waterflasks:heal_flask', {
        'type': 'minecraft:crafting_shaped',
        'pattern': pattern,
        'key': utils.item_stack_dict(ingredients, ''.join(pattern)[0]),
        'result': utils.item_stack(result)
    })

def damage_shaped(rm: ResourceManager, name_parts: utils.ResourceIdentifier, pattern: Sequence[str], ingredients: Json, result: Json, group: str = None, conditions: Optional[Json] = None) -> RecipeContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'data', res.domain, 'recipes', res.path), {
        'type': 'tfc:damage_inputs_shaped_crafting',
        'recipe': {
            'type': 'minecraft:crafting_shaped',
            'group': group,
            'pattern': pattern,
            'key': utils.item_stack_dict(ingredients, ''.join(pattern)[0]),
            'result': utils.item_stack(result),
            'conditions': utils.recipe_condition(conditions)
        }
    })
    return RecipeContext(rm, res)

def delegate_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, recipe_type: str, delegate: Json) -> RecipeContext:
    res = utils.resource_location(rm.domain, name_parts)
    rm.write((*rm.resource_dir, 'data', res.domain, 'recipes', res.path), {
        'type': recipe_type,
        'recipe': delegate
    })
    return RecipeContext(rm, res)


def heat_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: utils.Json, temperature: float, result_item: Optional[Union[str, Json]] = None, result_fluid: Optional[str] = None) -> RecipeContext:
    result_item = item_stack_provider(result_item) if isinstance(result_item, str) else result_item
    result_fluid = None if result_fluid is None else fluid_stack(result_fluid)
    return rm.recipe(('heating', name_parts), 'tfc:heating', {
        'ingredient': utils.ingredient(ingredient),
        'result_item': result_item,
        'result_fluid': result_fluid,
        'temperature': temperature
    })


def anvil_recipe(rm: ResourceManager, name_parts: utils.ResourceIdentifier, ingredient: Json, result: Json, tier: int, *rules: Rules, bonus: bool = None):
    rm.recipe(('anvil', name_parts), 'tfc:anvil', {
        'input': utils.ingredient(ingredient),
        'result': item_stack_provider(result),
        'tier': tier,
        'rules': [r.name for r in rules],
        'apply_forging_bonus': bonus
    })

def fluid_stack(data_in: Json) -> Json:
    if isinstance(data_in, dict):
        return data_in
    fluid, tag, amount, _ = utils.parse_item_stack(data_in, False)
    assert not tag, 'fluid_stack() cannot be a tag'
    return {
        'fluid': fluid,
        'amount': amount
    }


def item_stack_provider(data_in: Json = None, copy_input: bool = False, copy_heat: bool = False, copy_food: bool = False, reset_food: bool = False, add_heat: float = None, add_trait: str = None, remove_trait: str = None, empty_bowl: bool = False, copy_forging: bool = False, other_modifier: str = None) -> Json:
    if isinstance(data_in, dict):
        return data_in
    stack = utils.item_stack(data_in) if data_in is not None else None
    modifiers = [k for k, v in (
        ('tfc:copy_input', copy_input),
        ('tfc:copy_heat', copy_heat),
        ('tfc:copy_food', copy_food),
        ('tfc:reset_food', reset_food),
        ('tfc:empty_bowl', empty_bowl),
        ('tfc:copy_forging_bonus', copy_forging),
        (other_modifier, other_modifier is not None),
        ({'type': 'tfc:add_heat', 'temperature': add_heat}, add_heat is not None),
        ({'type': 'tfc:add_trait', 'trait': add_trait}, add_trait is not None),
        ({'type': 'tfc:remove_trait', 'trait': remove_trait}, remove_trait is not None)
    ) if v]
    if modifiers:
        return {
            'stack': stack,
            'modifiers': modifiers
        }
    return stack


def leather_knapping(rm: ResourceManager, name_parts: ResourceIdentifier, pattern: List[str], result: Json, outside_slot_required: bool = None):
    knapping_recipe(rm, name_parts, 'tfc:leather', pattern, result, None, outside_slot_required)

def knapping_recipe(rm: ResourceManager, name_parts: ResourceIdentifier, knap_type: str, pattern: List[str], result: Json, ingredient: Json, outside_slot_required: bool):
    for part in pattern:
        assert 0 < len(part) < 6, 'Incorrect length: %s' % part
    rm.recipe((knap_type.split(':')[1] + '_knapping', name_parts), 'tfc:knapping', {
        'knapping_type': knap_type,
        'outside_slot_required': outside_slot_required,
        'pattern': pattern,
        'ingredient': None if ingredient is None else utils.ingredient(ingredient),
        'result': utils.item_stack(result)
    })
