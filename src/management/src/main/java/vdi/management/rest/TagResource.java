package vdi.management.rest;

import java.util.ArrayList;
import java.util.List;

import vdi.commons.web.rest.interfaces.ManagementTagService;
import vdi.commons.web.rest.objects.ManagementTag;
import vdi.management.storage.DAO.TagsDAO;
import vdi.management.storage.entities.Tag;

public class TagResource implements ManagementTagService {

	@Override
	public List<ManagementTag> getTags() {
		List<Tag> tags = TagsDAO.getAllTags();
		List<ManagementTag> result = new ArrayList<ManagementTag>();

		for (Tag t : tags) {
			ManagementTag mt = new ManagementTag();
			mt.name = t.getName();
			mt.identifier = t.getSlug();
			result.add(mt);
		}

		return result;
	}

}
