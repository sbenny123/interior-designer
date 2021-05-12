package com.project.horizoninteriordesigner;


import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class ItemSelectionFragmentUnitTest {
   /* private TestableItemSelectionFragment fragment;
    private GridLayoutManager mockLayoutManager;


    @Before
    public void setUp() {
        mockLayoutManager = mock(GridLayoutManager.class);
        fragment = new TestableItemSelectionFragment(); // retrieves all items
        fragment.setLayoutManager(mockLayoutManager);
    }

    @Test
    public void defaultDisplay() {
        RecyclerView recyclerView = fragment.getView().findViewById(R.id.rv_items);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        //assertThat(LayoutManager layoutManager) is provided to us by the assertj-android-recyclerview library.
        assertThat(layoutManager).isEqualTo(mockLayoutManager);
    }




    //Here is the subclass of CandiesFragment that we use for testing.
    // It overrides getLayoutManager to return a mock so we can assert on the mock.

    public static class TestableItemSelectionFragment extends ItemSelectionFragment {
        private LinearLayoutManager mockLayoutManager;

        public void setLayoutManager(LinearLayoutManager mockLayoutManager) {
            this.mockLayoutManager = mockLayoutManager;
        }

        public LinearLayoutManager getLayoutManager() {
            return mockLayoutManager;
        }
    }*/
}
