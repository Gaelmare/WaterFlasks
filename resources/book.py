from patchouli import *
from i18n import I18n
from mcresources import ResourceManager

from mcresources import utils
from data import DROP_ENTITIES

class LocalInstance:
    INSTANCE_DIR = os.getenv('LOCAL_MINECRAFT_INSTANCE')  # The location of a local .minecraft directory, for testing in external minecraft instance (as hot reloading works much better)

    @staticmethod
    def wrap(rm: ResourceManager):
        def data(name_parts: ResourceIdentifier, data_in: JsonObject):
            return rm.write((LocalInstance.INSTANCE_DIR, '/'.join(utils.str_path(name_parts))), data_in)

        if LocalInstance.INSTANCE_DIR is not None:
            rm.data = data
            return rm
        return None

def generate(rm: ResourceManager):
    i18n = I18n.create('en_us')

    print('Writing book')
    make_book(rm, i18n)

    i18n.flush()

    if LocalInstance.wrap(rm):
        print('Copying into local instance at: %s' % LocalInstance.INSTANCE_DIR)
        make_book(rm, I18n.create('en_us'), local_instance=True)

    print('Done')



def make_book(rm: ResourceManager, i18n: I18n, local_instance: bool = False):

    book = Book(rm, 'field_guide', {}, i18n, local_instance, reverse_translate=False)
    book.template('leather_knapping_recipe', custom_component(0, 0, 'LeatherKnappingComponent', {'recipe': '#recipe'}), text_component(0, 99))

    book.category('waterflasks', 'Water Flasks', 'Have some better containers for carrying your drinks around with you.', 'waterflasks:iron_flask', is_sorted=True, entries=(
        entry('bladders', 'Flask Materials', 'waterflasks:bladder', pages=(
            text('Central to effective water storage is reuse of something that stored water(ish) before.$(br2)$(bold)       Bladders!$()$(br)Only certain animals have bladders that work for this purpose, and some species\' are easier to extract than others.').link('waterflasks:bladder'),
            text('$(br)$(bold){:_<12s}'.format('Animal') + '{:_>16s}'.format('Chance$(br)') +'$()'+''.join([('{0:_<16s}{1:_>10s}%').format(animal, DROP_ENTITIES[animal])+'$(br)' for animal in DROP_ENTITIES]) + '$(br)To increase your chances, use a higher damage weapon to butcher the animal.'),
            item_spotlight('waterflasks:leather_side', 'Leather Flask Side', text_contents='Flask Sides are used in both flask recipes.').link('waterflasks:leather_side'),
            leather_knapping('waterflasks:leather_knapping/leather_side', 'Leather flask sides are knapped out of leather to protect the bladder from punctures.'),
        )),
        entry('recipes', 'Flask Recipes', 'waterflasks:leather_flask', pages=(
            text('There are two tiers of water flasks.$(br)The leather flask holds 5 drinks and lasts for 100.$(br)The iron flask holds 20 drinks, and lasts for 400.$(br2)Both flasks may be repaired.'),
            crafting('waterflasks:crafting/leather_flask', 'waterflasks:crafting/iron_flask').link('waterflasks:leather_flask').link('waterflasks:iron_flask'),
            crafting('waterflasks:crafting/repair_leather', 'waterflasks:crafting/repair_iron', title='Repair Recipes'),
            crafting('waterflasks:crafting/repair_broken_leather', 'waterflasks:crafting/repair_broken_iron', title='Renew Recipes', text_contents='Flasks can also be repaired when completely broken.'),
        ))
    ))

    book.build()


